<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<link href="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<spring:url var="create" value="/back-office-profiles/create"/>
<spring:url var="copyAction" value="/back-office-profiles/copy"/>
<spring:url var="navigationAuths" value="/back-office-navigationProfile"/>

<script type="text/javascript">

var accentedCharacters = "àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇßØøÅåÆæœ";

   var users = [<c:forEach var="user" items="${users}">"${user.getName()} - ${user.getDisplayName()}",</c:forEach>];

	var profiles = {<c:forEach var="profile" items="${profiles}">"${profile.toGroup().getName()}" : "${profile.toGroup().getCod()}",</c:forEach>};
</script>


<jsp:include page="ui-autocomplete.jsp" />
<jsp:include page="profilesScript.jsp" />

<div class="row">

<div class="col-md-4">
	
	
	<form action="${create}" method="POST">
	  <div class="input-group">
	  	<div class="input-group-addon">
	        <span><spring:message code="label.profile" /></span>
	    </div>
	    <input type="text" class="form-control" id="profile" name="name" required>
	  </div>
	  <div class="input-group">
	    <div class="input-group-addon">
	        <span><spring:message code="label.type" /> </span>
	    </div>
	    <select class="form-control" id="type" name="type">
	      <c:forEach var="tp" items="${types}">
    		<option value="${tp.getType()}">${tp.getType()}</option>
    	  </c:forEach>
	    </select>
	  </div>
	  <input name="_csrf" value="${csrf.token}" hidden>

	  <button class="btn btn-primary" type="submit"><span class="glyphicon glyphicon-plus"></span> <spring:message code="label.create" /></button>
	</form>
	
	
</div>
</div>

<br>



<div class="col-lg-8">
	<c:forEach var="profile" items="${profiles}">
	
		<div class="accordion" onClick="loadTree(${profile.getExternalId()}, '${profile.getPresentationName()}')">
			${profile.getPresentationName()} - ${profile.getType().getType()}
		</div>
		
		<div class="accordion-panel"  id="${profile.getExternalId()}">
			
			<div>
				<div class="col-lg-6"></div>
				<div class="col-lg-6">
					<form class="form-horizontal" action="${copyAction}" method="POST">
						<label class="control-label"><spring:message code="label.copyFrom" /></label>
						<input id="groupInp" class=" groupInp autocomplete" required>
						<input id="groupInpCod" name="groupFrom" type="hidden">
						<input id="groupId" name="groupTo" value="${profile.toGroup().getCod()}" type="hidden">
						<button class="btn btn-primary" type="submit"><spring:message code="label.copy" /></button>
					</form>
				</div>
			</div>
			
			<div id="${profile.getExternalId()}" class="small">
			<table class="table table-auths ui-droppable" >
		  	  <thead>
		  			<tr>
		  				<th><spring:message code="label.authorizations.profile"/></th>
			  			<th><spring:message code="portal.academicAdminOffice"/></th>
			  			<th><spring:message code="label.degrees"/></th>
		  			</tr>
		  			
		  		</thead>
		  		<tbody id="${profile.getExternalId()}">		
			  		<c:forEach var="auth" items="${profilesAuths.get(profile.getExternalId())}">
			  			<tr class="authorizations ui-droppable" id="${auth.externalId}">
			  				<td>
				  				<div class="authtip">
				  					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.getPresentationName()}" data-auth-id="${auth.getExternalId()}" data-auth-name="${auth.getOperation().getLocalizedName()}" data-type="auth" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>
				  						${auth.operation.localizedName}
				  						<span class="glyphicon glyphicon-remove"></span>
				  					</button>
				  				
				  					<span class="authtiptext">
				  						<ul>
											<c:forEach var="menu" items="${authsMenus.get(auth.getOperation().toString())}">
												<li>${menu}</li>
											</c:forEach>
										</ul>
									</span>
				  				</div>
			  				</td>
			  				<td>
			  					<table class="office-list">
			  						<c:forEach var="office" items="${auth.office}">
			  							<tr style="bottom:1px" id="${office.externalId}">
			  								<td>
			  									<button data-office-id="${office.externalId}" data-office-name="${office.name.content}" data-auth-id="${auth.getExternalId()}" data-auth-name="${auth.getOperation().getLocalizedName()}" data-type="office" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>
			  										${office.name.content} 
			  										<span class="glyphicon glyphicon-remove"></span>
			  									</button>
			  								</td>
			  							</tr>
			  						</c:forEach>
			  					</table>
			  				</td>
			  				<td>
								<table class="program-list">
			  						<c:forEach var="program" items="${auth.program}">
			  							<tr style="bottom:1px" id="${program.externalId}" >
			  								<td>
			  									<button data-program-id="${program.externalId}" data-program-name="${program.presentationName}" data-auth-id="${auth.getExternalId()}" data-auth-name="${auth.getOperation().getLocalizedName()}" data-type="program" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>
			  										${program.presentationName} 
			  										<span class="glyphicon glyphicon-remove"></span>
			  									</button>
			  								</td>
			  							</tr>
			  						</c:forEach>
			  					</table>
							</td>
			  			</tr>
			  		</c:forEach>
			  		<tr class="authorizations ui-droppable" style="height: 15px;">
			  			<td></td>
			  			<td></td>
			  			<td></td>
			  		</tr>
			  	</tbody>
		  	</table>
		</div>
		
			<div class="box menus" >
			<header class="headerProfile"><spring:message code="label.menus" /></header>
				
				<div class="tree${profile.getExternalId()}"  style="broder: none;">
				</div>				
			</div>	

			
			<div class="box subprofiles">
				<header class="headerProfile"><spring:message code="label.subProfiles" /></header>
				<c:forEach var="subProfile" items="${subProfiles.get(profile.getExternalId())}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.getPresentationName()}" data-child-id="${subProfile.getExternalId()}" data-user-name="${subProfile.getPresentationName()}"  data-type='child' class='btn btn-default btn-box' data-toggle='modal' data-target='#confirmDelete' title=<spring:message code="label.delete"/>> ${subProfile.getPresentationName()} <span class="glyphicon glyphicon-remove"></span> </button>
				</c:forEach>
			</div>
			
			
			<div class="box users ui-droppable">
			<header class="headerProfile"><spring:message code="label.users" /></header>
				<c:forEach var="user" items="${profilesUsers.get(profile.getExternalId())}">
					<button data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.getPresentationName()}" data-user-id="${user.getExternalId()}" data-user-name="${user.getUsername()} - ${user.getDisplayName()}" data-type="user" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title=<spring:message code="label.delete"/>>${user.getUsername()} - ${user.getDisplayName()} <span class="glyphicon glyphicon-remove"></span></button>
				</c:forEach>
			</div>
			
			
			<button style="float: right" data-profile-id="${profile.getExternalId()}" data-profile-name="${profile.getPresentationName()}" data-type="profile" data-toggle="modal" data-target="#confirmDelete" class="btn btn-danger" title=<spring:message code="label.delete"/>><spring:message code="label.delete" /> <spring:message code="label.profile" />  <span class="glyphicon glyphicon-remove"></span></button>
	
	
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
				
				<div id="filter-auths" class="input-group">
					<div class="input-group-addon">
				       <span><spring:message code="label.filter" /></span>
				    </div>
				    <input type="text"  class="form-control">
				    
				    <script type="text/javascript">
						$("#filter-auths").find("input").keyup(function() {
						  $("#filter-auths").next().children().each(function () {
							  
							 matcher = new RegExp("[ A-Z-@.()"+ accentedCharacters+"]*" + $("#filter-auths").find("input").val().toLowerCase().split(" ").join("[ A-Z-@.()"+ accentedCharacters+"]*") + "[ A-Z-@.()"+ accentedCharacters+"]*" , "gi" );

						    if(!matcher.test($(this).find("#presentationName").html().toLowerCase())){
								$(this).hide();
						    }else{
						    	$(this).show();
						    }
						  });
						});
					</script>
				    
				</div>

				<div class="panel-body scrollable">
					<c:forEach var="operation" items="${operations}">
						<a href="${navigationAuths}?operation=${operation}">
							<div class="draggable_course authorization">
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
					<a data-toggle="collapse" data-parent="#cursos_acc" data-target="#collapseTwo">
						<spring:message code="portal.academicAdminOffice"/>
					</a>
				</h3>
			</div>
			<div id="collapseTwo" class="panel-collapse collapse">
			
				<div id="filter-offices" class="input-group">
					<div class="input-group-addon">
				       <span><spring:message code="label.filter" /></span>
				    </div>
				    <input type="text"  class="form-control">
				    
				    <script type="text/javascript">
						$("#filter-offices").find("input").keyup(function() {
						  $("#filter-offices").next().children().each(function () {
							  

							matcher = new RegExp("[ A-Z-@.()"+ accentedCharacters+"]*" + $("#filter-offices").find("input").val().toLowerCase().split(" ").join("[ A-Z-@.()"+ accentedCharacters+"]*") + "[ A-Z-@.()"+ accentedCharacters+"]*" , "gi" );

							if(!matcher.test($(this).find("#presentationName").html().toLowerCase())){
								$(this).hide();
						    }else{
						    	$(this).show();
						    }
						  });
						});
					</script>
				    
				</div>
			
			
				<div class="panel-body scrollable">
					<c:forEach var="office" items="${offices}">
						<div class="draggable_course office">
							<div id="oid" style="display:none">${office.oid}</div>
							<div id="presentationName" style="display:none">${office.unit.name}</div>
							<div id="name">${office.unit.name}</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#cursos_acc" data-target="#collapseThree">
						<spring:message code="label.degrees"/>
					</a>
				</h3>
			</div>
			<div id="collapseThree" class="panel-collapse collapse">
			
				<div id="filter-degree" class="input-group">
					<div class="input-group-addon">
				       <span><spring:message code="label.filter" /></span>
				    </div>
				    <input type="text"  class="form-control">
				    
				    <script type="text/javascript">
						$("#filter-degree").find("input").keyup(function() {
						  $("#filter-degree").next().children().each(function () {

							matcher = new RegExp("[ A-Z-@.()"+ accentedCharacters+"]*" + $("#filter-degree").find("input").val().toLowerCase().split(" ").join("[ A-Z-@.()"+ accentedCharacters+"]*") + "[ A-Z-@.()"+ accentedCharacters+"]*" , "gi" );

							if(!matcher.test($(this).find("#presentationName").html().toLowerCase())){
								$(this).hide();
						    }else{
						    	$(this).show();
						    }
						  });
						});
					</script>
				    
				</div>
			
				<div class="panel-body scrollable">
					<c:forEach var="degree" items="${degrees}">
						<div class="draggable_course program">
							<div id="oid" style="display:none">${degree.oid}</div>
							<div id="presentationName" >${degree.getCode()} - ${degree.presentationName}</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#cursos_acc" data-target="#collapseFour">
						<spring:message code="label.menus"/>
					</a>
				</h3>
			</div>
			<div id="collapseFour" class="panel-collapse collapse">
				
				<div id="filter-menus" class="input-group">
					<div class="input-group-addon">
				       <span><spring:message code="label.filter" /></span>
				    </div>
				    <input type="text"  class="form-control">
				    
				    <script type="text/javascript">
						$("#filter-menus").find("input").keyup(function() {
						  $("#filter-menus").next().find(".fancytree-container").children().each(function () {
						    
							matcher = new RegExp("[ A-Z-@.()"+ accentedCharacters+"]*" + $("#filter-menus").find("input").val().toLowerCase().split(" ").join("[ A-Z-@.()"+ accentedCharacters+"]*") + "[ A-Z-@.()"+ accentedCharacters+"]*" , "gi" );

							if(!matcher.test($(this).find("#path").html().toLowerCase())){
								
								var cond = true;
								$(this).find("#path").each(function(){
									if(matcher.test($(this).html().toLowerCase())){
										cond = false;
									}
								})

								if(cond){
									$(this).hide();	
								}
						    }else{
						    	$(this).show();
						    }
						  });
						});
					</script>
				    
				</div>
			
				<div class="panel-body scrollable">
					<div id="menus"></div>
				</div>
			</div>
		</div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#auths" data-target="#collapseFive">
						<spring:message code="title.Accesscontrol.Profiles" />
					</a>
				</h3>
			</div>
			<div id="collapseFive" class="panel-collapse collapse">
			
				<div id="filter-profile" class="input-group">
					<div class="input-group-addon">
				       <span><spring:message code="label.filter" /></span>
				    </div>
				    <input type="text"  class="form-control">
				    
				    <script type="text/javascript">
						$("#filter-profile").find("input").keyup(function() {
						  $("#filter-profile").next().children().each(function () {
							matcher = new RegExp("[ A-Z-@.()"+ accentedCharacters+"]*" + $("#filter-profile").find("input").val().toLowerCase().split(" ").join("[ A-Z-@.()"+ accentedCharacters+"]*") + "[ A-Z-@.()"+ accentedCharacters+"]*" , "gi" );

							if(!matcher.test($(this).find("#name").html().toLowerCase())){
								$(this).hide();
						    }else{
						    	$(this).show();
						    }
						  });
						});
					</script>
				    
				</div>
			
				<div class="panel-body scrollable">
					<c:forEach var="profile" items="${profiles}">
						<div class="draggable_course profile">
							<div id="oid" style="display:none">${profile.oid}</div>
							<div id="name">${profile.getPresentationName()}</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					<a data-toggle="collapse" data-parent="#auths" data-target="#collapseSix">
						<spring:message code="label.users" />
					</a>
				</h3>
			</div>
			<div id="collapseSix" class="panel-collapse collapse">
			
				<div id="filter-users" class="input-group">
					<div class="input-group-addon">
				       <span><spring:message code="label.filter" /></span>
				    </div>
				   
				    <input type="text"  class="form-control">

				</div>
				

				<div id="usersResults" class="panel-body scrollable">
				</div>
				
			</div>
		</div>
		
	</div>
</div>


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


<!-- Modal Dialog to childNotification-->
<div class="modal fade" id="childNotification" role="dialog" aria-labelledby="childNotification" aria-hidden="true">
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
		<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.close"/></button>
      </div>
    </div>
  </div>
</div>