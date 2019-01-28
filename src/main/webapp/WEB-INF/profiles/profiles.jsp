<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<spring:url var="create" value="/profiles/create"/>
<spring:url var="navigation" value="/navigation/accessGroup"/>

<script type="text/javascript">
var users = [<c:forEach var="user" items="${users}">"${user.getName()}",</c:forEach>];
</script>

<jsp:include page="ui-autocomplete.jsp" />
<jsp:include page="profilesScript.jsp" />

<div class="row">
<div class="col-md-4">
	<form class="form-horizontal" action="${create}" method="POST">
		<label class="control-label"><spring:message code="label.profile" /></label>
		<input name="name">
		<input name="_csrf" value="${csrf.token}" hidden>
		<button class="btn btn-primary" type="submit"><span class="glyphicon glyphicon-plus"></span> <spring:message code="label.create" /></button>
	</form>
</div>
</div>

<br>



<div class="col-lg-8">
	<c:forEach var="profile" items="${profiles}">
	
		<button class="accordion" >${profile.name}</button>
		<div class="accordion-panel" id="${profile.getExternalId()}">
			
				<header><spring:message code="label.authorizations" /></header>
				<div class="box authorizations ui-droppable">
					<c:forEach var="auth" items="${profile.getAuthSet()}">
						<button>${auth.getOperation().localizedName} <span class="glyphicon glyphicon-remove"></span></button>
					</c:forEach>
				</div>
		
			
				<header><spring:message code="label.groups" /></header>
				<div class="box groups ui-droppable">
					<c:forEach var="group" items="${profile.getGroupSet()}">
						<tr><td><button>${group.expression()} <span class="glyphicon glyphicon-remove"></span></button></td></tr>
					</c:forEach>
				</div>
			
			
				<header><spring:message code="label.users" /></header>
				<div class="box users ui-droppable">
					<c:forEach var="user" items="${profile.getMemberSet()}">
						<tr><td><button>${user.getUsername()} <span class="glyphicon glyphicon-remove"></span></button></td></tr>
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
						<spring:message code="label.authorizations" />
					</a>
				</h3>
			</div>
			<div id="collapseOne" class="panel-collapse collapse">
				<div class="panel-body">
					<c:forEach var="operation" items="${operations}">
						<a href="${navigation}?operation=${operation}">
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
						<a href='${navigation}?expression=%23${group.expression().substring(1)}'>
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



