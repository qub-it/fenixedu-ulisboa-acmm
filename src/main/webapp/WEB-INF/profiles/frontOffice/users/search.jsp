<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<link href="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<spring:url var="searchAction" value="/front-office-users/search"/>
<spring:url var="copyAction" value="/front-office-users/copy"/>
<spring:url var="navigationGroup" value="/front-office-navigationProfile/accessGroup"/>
<spring:url var="getTree" value="/back-office-users/getTree"/>

<script type="text/javascript">
var users = [<c:forEach var="user" items="${users}">"${user.getName()} - ${user.getDisplayName()}",</c:forEach>];
</script>


<jsp:include page="ui-autocomplete.jsp" />
<jsp:include page="usersSearchScript.jsp" />


<div class="col-md-4">
	<form class="form-horizontal" action="${searchAction}" method="GET">
		<label class="control-label"><spring:message code="label.username" /></label>
		<input id="userInp" name="username" class="autocomplete">
		<button class="btn btn-primary" type="submit"><spring:message code="label.search" /></button>
	</form>
</div>

<c:if test="${user!=null}">

	<div>
		<form class="form-horizontal" action="${copyAction}" method="GET">
			<label class="control-label"><spring:message code="label.copyFrom" /></label>
			<input id="userInp2" name="usernameFrom" class="autocomplete">
			<input id="userId" name="usernameTo" value="${user.username}" type="hidden">
			<button class="btn btn-primary" type="submit"><spring:message code="label.copy" /></button>
		</form>
	</div>


	<h1>
		${user.username}
	</h1>
		
	<div class="col-lg-8" >
		<header class="headerProfile"><spring:message code="title.Accesscontrol.Profiles" /></header>
		<div class="box" id="${user.getExternalId()}">
			<c:forEach var="profile" items="${profiles}">
				<button id="${profile.getExternalId()}" data-profile="${profile.toGroup().getName()}" data-profile-id="${profile.getExternalId()}" data-user="${user.getName()}" data-user-id="${user.getExternalId()}" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>
					${profile.toGroup().getName()} <span class="glyphicon glyphicon-remove"></span>
				</button>
			</c:forEach>
		</div>
		
		<header class="headerProfile"><spring:message code="label.menus" /></header>
			<div class="box menus" >
			
				<div class="tree" data-user="${user.getExternalId()}"  style="broder: none;">
				</div>				
			</div>	
		
	</div>
	
	<div class="col-lg-4" style="float:right">
		<div class="panel-group" id="profiles" data-offset-top="200">
		
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">
						<a data-toggle="collapse" data-parent="#profiles" data-target="#collapseOne">
							<spring:message code="title.Accesscontrol.Profiles" />
						</a>
					</h3>
				</div>
				<div id="collapseOne" class="panel-collapse collapse">
					
					<div id="filter-profile" class="input-group">
						<div class="input-group-addon">
					       <span><spring:message code="label.filter" /></span>
					    </div>
					    <input type="text"  class="form-control">
					    
					    <script type="text/javascript">
							$("#filter-profile").find("input").keyup(function() {
							  $("#filter-profile").next().children().each(function () {
							    if(!$(this).find("#name").html().toLowerCase().includes($("#filter-profile").find("input").val().toLowerCase())){
									$(this).hide();
							    }else{
							    	$(this).show();
							    }
							  });
							});
						</script>
					    
					</div>
				
				
					<div class="panel-body scrollable">
						<c:forEach var="profile" items="${profileSet}">
							<div class="draggable profile">
								<div id="name">${profile.toGroup().getName()}</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>		
		</div>
	</div>
	
	
</c:if>


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
