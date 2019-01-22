<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>


<c:forEach var="profile" items="${profiles}">
	<h2>${profile.name}</h2>
	
	<br>
	auths:
	<ul>
		<c:forEach var="auth" items="${profile.getAuthSet()}">
			<li>${auth.getOperation()}</li>
		</c:forEach>
	</ul>
	
	<br>
	groups:
	<ul>
		<c:forEach var="group" items="${profile.getGroupSet()}">
			<li>${group.expression()}</li>
		</c:forEach>
	</ul>
	
	<br>
	users:
	<ul>
		<c:forEach var="user" items="${profile.getMemberSet()}">
			<li>${user.getUsername()}</li>
		</c:forEach>
	</ul>
</c:forEach>
