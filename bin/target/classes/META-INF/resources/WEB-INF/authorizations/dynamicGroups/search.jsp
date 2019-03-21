<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<spring:url var="searchAction" value="/dynamic-groups/search"/>
<spring:url var="copyAction" value="/dynamic-groups/search/copy"/>
<spring:url var="navigation" value="/navigation/accessGroup"/>
<spring:url var="modifyOffice" value="/dynamic-groups/modifyOffice"/>
<spring:url var="modifyProgram" value="/dynamic-groups/modifyProgram"/>


<script type="text/javascript">
var users = [<c:forEach var="user" items="${users}">"${user}",</c:forEach>];

</script>


<jsp:include page="ui-autocomplete.jsp" />
<jsp:include page="authorizationsScriptsSearch.jsp" />


<div class="col-md-4">
	<form class="form-horizontal" action="${searchAction}" method="GET">
		<label class="control-label"><spring:message code="label.username" /></label>
		<input id="userInp" name="username" class="autocomplete">
		<button class="btn btn-primary" type="submit"><spring:message code="label.search" /></button>
	</form>
</div>




<c:if test="${user!=null}">

	<div >
		<form class="form-horizontal" action="${copyAction}" method="GET">
			<label class="control-label"><spring:message code="label.copyFrom" /></label>
			<input id="userInp2" name="copyFromUsername" class="autocomplete">
			<input id="userId" name="username" value="${user.username}" type="hidden">
			<button class="btn btn-primary" type="submit"><spring:message code="label.copy" /></button>
		</form>
	</div>


	<h1>
		${user.username}
	</h1>
	
		<div class="col-lg-8" >
			<div class="small">
				<table id="${user.externalId}" class="table" >
					<thead>
						<tr>
							<th>grupos</th>
						<tr>
					</thead>
					<tbody>
						<c:forEach var="group" items="${groups}">
							<tr class="auth ui-droppable" id="${group.externalId}">
								<td>
									<button style="margin-bottom: 2px;" data-group-id="${group.externalId}" data-url="${revokeGroup}" data-user-name="${user.username}" data-user-id="${user.externalId}" data-group-expression="${group.expression()}" data-toggle="modal" data-target="#confirmRevokeGroup" class="btn btn-default" title=<spring:message code="label.delete"/>>
									${group.expression()} <span class="glyphicon glyphicon-remove"></span>
									</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>	
				</table>
			</div>
		</div>
</c:if>	
	
<div class="col-lg-4" style="float:right">
	<div class="panel-group" id="groups" data-offset-top="200">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="groups" data-target="#collapseOne">
						<spring:message code="label.groups" />
					</a>
				</h3>
			</div>
			<div id="collapseOne" class="panel-collapse collapse">
				<div class="panel-body">
					<c:forEach var="dynamicGroup" items="${dynamicGroups}">
						<a href='${navigation}?expression=%23${dynamicGroup.expression().substring(1)}'>
							<div class="draggable_course authorization">
								<div id="groupName">${dynamicGroup.expression()}</div>
								<div id="groupId" style="display:none">${dynamicGroup.externalId}</div>
							</div>
						</a>
					</c:forEach>
				</div>
			</div>
		</div>		
	</div>
</div>

<!-- Modal Dialog to delete authorization-->
<div class="modal fade" id="confirmRevokeGroup" role="dialog" aria-labelledby="confirmDeleteLabel" aria-hidden="true">
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
	