<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url var="userURL" value="/academic-authorizations/search"/>


<h1>${expression}</h1>

users
<ul>
	<c:forEach var="user" items="${users}">
		<li><a href="${userURL}?username=${user.getName()}">${user.getName()}</a></li>
	</c:forEach>
</ul>

menus
<ul>
	<c:forEach var="menu" items="${menus}">
		<li>${menu.getTitle().getContent()}</li>
	</c:forEach>
</ul>