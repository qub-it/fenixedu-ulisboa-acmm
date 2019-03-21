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
	
		<div class="accordion">${profile.name}</div>
		
		<div class="accordion-panel" id="${profile.getExternalId()}">
		
			<div>
				<div class="col-lg-10"></div>
				<div class="col-lg-2">
					<button type="button" data-profile-id="${profile.getExternalId()}" data-toggle="modal" data-target="#copy" class="btn btn-default">
			        	<span class="glyphicon glyphicon-copy"></span> <spring:message code="label.copy" /> 
			      	</button>
				</div>
			</div>

			<header><spring:message code="label.authorizations.profile" /></header>
			<div class="box authorizations ui-droppable">
				<c:forEach var="auth" items="${profile.getAuthSet()}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.name}" data-auth-id="${auth.getOperation()}" data-auth-name="${auth.getOperation().localizedName}" data-type="auth" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>${auth.getOperation().localizedName} <span class="glyphicon glyphicon-remove"></span></button>
				</c:forEach>
			</div>

			<header><spring:message code="label.groups" /></header>
			<div class="box groups ui-droppable">
				<c:forEach var="group" items="${profile.getGroupSet()}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.name}" data-group-id="${group.getExternalId()}" data-group-name="${group.expression()}" data-type="group" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>${group.expression()} <span class="glyphicon glyphicon-remove"></span></button>
				</c:forEach>
			</div>
					
			<header><spring:message code="label.users" /></header>
			<div class="box users ui-droppable">
				<c:forEach var="user" items="${profile.getMemberSet()}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.name}" data-user-id="${user.getExternalId()}" data-user-name="${user.getUsername()}" data-type="user" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>${user.getUsername()} <span class="glyphicon glyphicon-remove"></span></button>
				</c:forEach>
			</div>
			
			<div>
				<div class="col-lg-10"></div>
				<div id="box" class="col-lg-2">
				<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.name}" data-type="profile" data-toggle="modal" data-target="#confirmDelete" class="btn btn-danger" title=<spring:message code="label.delete"/>><spring:message code="label.delete"/> <span class="glyphicon glyphicon-remove"></span></button>
				</div>
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
						<spring:message code="label.groups" />
					</a>
				</h3>
			</div>
			<div id="collapseTwo" class="panel-collapse collapse">
				<div class="panel-body">
					<c:forEach var="group" items="${groups}">
						<a href='${navigationGroup}?expression=%23${group.expression().substring(1)}'>
							<div class="draggable_course group">
								<div id="groupName">${group.expression()}</div>
								<div id="groupId" style="display:none">${group.externalId}</div>
							</div>
						</a>
					</c:forEach>
				</div>
			</div>
		</div>
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#auths" data-target="#collapseThree">
						<spring:message code="label.users" />
					</a>
				</h3>
			</div>
			<div id="collapseThree" class="panel-collapse collapse">
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


<!-- Modal Dialog to delete authorization-->
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

<!-- Modal Dialog to delete authorization-->
<div class="modal fade" id="copy" role="dialog" aria-labelledby="copy" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title"><spring:message code="label.copyprofile"/></h4>
      </div>
      <div class="modal-body">
      	<div class="input-group" data-provide="copyFrom">
		    <div class="input-group-addon">
		        <spring:message code="label.copyfrom"/>
		    </div>
		    <select id="profileFrom" class="copyFrom form-control">
	      		<option value=""><spring:message code="label.selectoption"/></option>
	      		<c:forEach var="profile" items="${profiles}">
	      			<option value="${profile.getExternalId()}">${profile.name}</option>
	      		</c:forEach>
	      	</select>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="cancel" data-dismiss="modal"><spring:message code="label.cancel"/></button>
		<button type="button" class="btn btn-success" id="confirmCopy"><spring:message code="label.copy"/></button>
      </div>
    </div>
  </div>
</div>

