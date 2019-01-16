<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<spring:url var="searchAction" value="/dynamic-groups/search"/>
<spring:url var="copyAction" value="/dynamic-groups/search/copy"/>
<spring:url var="navigation" value="/academic-admin-office/academic-administration/navigation"/>
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
	
	
	
	
	
		
		<div class="small">
			<table  class="table" >
				<thead>
					<tr>
						<th>grupos</th>
					<tr>
				</thead>
				<tbody>
					<c:forEach var="group" items="${groups}">
						<tr>
							<td><button>${group.expression()} <span class="glyphicon glyphicon-remove"></span></button></td>
						</tr>
					</c:forEach>
				</tbody>	
			</table>
		</div>
		
	</div>
	
	

</c:if>