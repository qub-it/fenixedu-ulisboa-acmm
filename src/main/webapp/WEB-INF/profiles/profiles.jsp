<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<spring:url var="create" value="/profiles/create"/>
<spring:url var="navigationAuths" value="/access-control/accessControl/navigation"/>
<spring:url var="navigationGroup" value="/navigation/accessGroup"/>

<script type="text/javascript">
var users = [<c:forEach var="user" items="${users}">"${user.getName()}",</c:forEach>];
</script>

<jsp:include page="ui-autocomplete.jsp" />
<jsp:include page="profilesScript.jsp" />

<div class="row">
<div class="col-md-4">
	<form class="form-horizontal" action="${create}" method="POST">
		<label class="control-label"><spring:message code="label.profile" /></label>
		<input name="name" required>
		<input name="_csrf" value="${csrf.token}" hidden>
		<button class="btn btn-primary" type="submit"><span class="glyphicon glyphicon-plus"></span> <spring:message code="label.create" /></button>
	</form>
</div>
</div>

<br>



<div class="col-lg-8">
	<c:forEach var="profile" items="${profiles}">
	
		<div class="accordion">${profile.getPresentationName()}</div>
		
		<div class="accordion-panel" id="${profile.getExternalId()}">
	
			<header><spring:message code="label.authorizations.profile" /></header>
			<div class="box authorizations ui-droppable">
				<c:forEach var="auth" items="${profilesAuths.get(profile.getExternalId())}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.getPresentationName()}" data-auth-id="${auth.getExternalId()}" data-auth-name="${auth.getOperation().getLocalizedName()}" data-type="auth" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>${auth.getOperation().getLocalizedName()} <span class="glyphicon glyphicon-remove"></span></button>
				</c:forEach>
			</div>
	
			<header><spring:message code="label.users" /></header>
			<div class="box users ui-droppable">
				<c:forEach var="user" items="${profilesUsers.get(profile.getExternalId())}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.getPresentationName()}" data-user-id="${user.getExternalId()}" data-user-name="${user.getUsername()}" data-type="user" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>${user.getUsername()} <span class="glyphicon glyphicon-remove"></span></button>
				</c:forEach>
			</div>			
	
		</div>

	</c:forEach>
</div>

<div class="col-lg-4" style="float:right">
	<div class="panel-group" id="auths" data-offset-top="200">
	
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#auths" data-target="#collapseOne">
						<spring:message code="label.authorizations.profile" />
					</a>
				</h3>
			</div>
			<div id="collapseOne" class="panel-collapse collapse">
				<div class="panel-body">
					<c:forEach var="operation" items="${operations}">
						<a href="${navigationAuths}?operation=${operation}">
							<div class="draggable_course authorization">
								<c:if test="${operation.critical}">
									<div id="warning">${operation.criticalDescription}</div>
								</c:if>
								<div id="presentationName">${operation.localizedName}</div>
								<div id="operationName" style="display:none">${operation}</div>
							</div>
						</a>
					</c:forEach>
				</div>
			</div>
		</div>
		
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#auths" data-target="#collapseTwo">
						<spring:message code="label.users" />
					</a>
				</h3>
			</div>
			<div id="collapseTwo" class="panel-collapse collapse">
				<div class="panel-body">
					<form class="form-horizontal" id="userForm">
						<label class="control-label"><spring:message code="label.username" /></label>
						<input id="userInp" name="username" class="autocomplete">
						<button class="btn btn-primary" type="submit"><spring:message code="label.search" /></button>
					</form>
				</div>
			</div>
		</div>
		
	</div>
</div>


<!-- Modal Dialog to delete-->
<div class="modal fade" id="confirmDelete" role="dialog" aria-labelledby="confirmDelete" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title"><spring:message code="label.spaces.delete.title"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="label.spaces.delete.message"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="cancel" data-dismiss="modal"><spring:message code="label.cancel"/></button>
		<button type="button" class="btn btn-danger" id="confirm"><spring:message code="label.delete"/></button>
      </div>
    </div>
  </div>
</div>

