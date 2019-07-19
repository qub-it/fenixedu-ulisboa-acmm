<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<link href="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<jsp:include page="navigationScript.jsp" />

<h3>${operation.getLocalizedName()}</h3>
<div id="operation" style="display:none">${operation}</div>

<div class="col-lg-12">

	<header><spring:message code="label.users" /></header>
	<div class="box users ui-droppable">
		<c:forEach var="user" items="${users}">
			<div class="btn btn-default btn-box">${user.key}</div>
		</c:forEach>
	</div>

	<header><spring:message code="label.menus" /></header>
	<div class="tree"  style="broder: none;"></div>
</div>

<script>


$(document).ready(function() {
	loadTree("${operation}");	
});

</script>


