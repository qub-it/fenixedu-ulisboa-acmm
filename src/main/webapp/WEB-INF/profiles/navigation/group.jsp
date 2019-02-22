<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url var="copyAction" value="/navigationProfile/accessGroup/copy"/>

<script type="text/javascript">
var users = [<c:forEach var="user" items="${usersList}">"${user.getName()}",</c:forEach>];
</script>

<script type="text/javascript">
var groups = [<c:forEach var="group" items="${groups}">"${group.toGroup().getName()}",</c:forEach>];
</script>

<jsp:include page="ui-autocomplete.jsp" />
<jsp:include page="groupScript.jsp" />

<h3 id="expression">${group.getExpression()}</h3>

<div>
	<form class="form-horizontal" action="${copyAction}" method="GET">
		<label class="control-label"><spring:message code="label.copyFrom" /></label>
		<input id="groupInp" name="groupFrom" class="autocomplete">
		<input id="groupId" name="groupTo" value="${group.getName()}" type="hidden">
		<button class="btn btn-primary" type="submit"><spring:message code="label.copy" /></button>
	</form>
</div>

<div class="col-lg-8">

	<header><spring:message code="label.users" /></header>
	<div class="box users ui-droppable">
		<c:forEach var="user" items="${users}">
			<button id="${user.externalId}" data-user-id="${user.externalId}" data-user-name="${user.getUsername()}" data-toggle="modal" data-target="#confirmDelete" data-type="user" class="btn btn-default btn-box" title=<spring:message code="label.delete"/>>${user.getUsername()} <span class="glyphicon glyphicon-remove"></span></button>
		</c:forEach>
	</div>

	<header><spring:message code="label.menus" /></header>
	<div class="box menus ui-droppable">
		<c:forEach var="menu" items="${menus}">
			<button id="${menu.externalId}" data-menu-id="${menu.externalId}" data-menu-name="${menu.getTitle().getContent()}" data-toggle="modal" data-target="#confirmDelete" data-type="menu" class="btn btn-default btn-box" title=<spring:message code="label.delete"/>>${menu.getTitle().getContent()} <span class="glyphicon glyphicon-remove"></span></button>
		</c:forEach>
	</div>

</div>

<div class="col-lg-4" style="float:right">
	<div class="panel-group" id="auths" data-offset-top="200">
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#auths" data-target="#collapseTwo">
						<spring:message code="label.menus" />
					</a>
				</h3>
			</div>
			<div id="collapseTwo" class="panel-collapse collapse">
				<div class="panel-body">
					<c:forEach var="menu" items="${menusList}">
						<div class="draggable_course menu">
							<div id="menuName">${menu.getTitle().getContent()}</div>
							<div id="menuId" style="display:none">${menu.externalId}</div>
						</div>
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

