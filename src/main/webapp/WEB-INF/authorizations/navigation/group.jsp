<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url var="userURL" value="/search-authorizations/search"/>


<h1>${expression}</h1>

users
<ul>
<c:forEach var="user" items="${users}">

	<li><a href="${userURL}?user=${user.value}">${user.key}</a></li>


</c:forEach>


</ul>