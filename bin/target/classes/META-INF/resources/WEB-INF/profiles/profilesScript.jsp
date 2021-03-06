
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<style>

#box {
	margin-top: 10px;
}


#auths.affix {
	top: 20px;
}

#auths {
	max-height: 90%;
	min-height: 400px;
	overflow-y: auto;
	min-width: 25%;
}

#warning {
  display: none;
}

.draggable_course {
	padding: 5px;
	display: block;
	cursor: pointer;
}

.draggable_course:hover {
	background: rgba(255, 180, 23, 0.2);
}


.box {
  width: 100%;
  height: 100%;
  padding: 10px;
/*   border: 1px solid #44444487; */
}


.accordion {
  background-image: -webkit-gradient( linear, left bottom, left top, color-stop(1, rgb(215,227,140)), color-stop(0, rgb(202,215,127)) );
  color: #444;
  padding: 15px;
  width: 100%;
  border: none;
  border-radius: 5px;
  margin: 2px;
  text-align: left;
  outline: none;
  font-size: 15px;
  transition: 0.4s;
}

.activeAccordion, .accordion:hover {
  background-image: -webkit-gradient( linear, left bottom, left top, color-stop(1, rgb(202,215,127)), color-stop(0, rgb(183, 193, 122)) );
}

.accordion-panel {
  padding: 15px;
  display: none;
  background-color: white;
  overflow: hidden;
}
</style>


<script src="${pageContext.request.contextPath}/javaScript/jquery/jquery-ui.js"></script>


<spring:url var="addAuth" value="/profiles/addAuth"/>
<spring:url var="removeAuth" value="/profiles/removeAuth"/>
<spring:url var="addGroup" value="/profiles/addGroup"/>
<spring:url var="removeGroup" value="/profiles/removeGroup"/>
<spring:url var="addMember" value="/profiles/addMember"/>
<spring:url var="removeMember" value="/profiles/removeMember"/>
<spring:url var="delete" value="/profiles/delete"/>
<spring:url var="copy" value="/profiles/copy"/>

<script>

	function dropFunction(event, ui) {
		if(!$(ui.draggable).hasClass("course-dragging"))
			return;
		
		var profile = $(this).parent().attr("id");
		
		if($(ui.draggable).hasClass("authorization") && $(this).hasClass("authorizations")){
			var authName = $(ui.draggable).children('#presentationName').html();
			var operation = $(ui.draggable).children('#operationName').html();
			
			var obj = $(this);
			
			$.ajax({
	    		  	data: {"profile" : profile, "operation": operation, "validity": "9999-12-31"},
	                url: "${addAuth}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.append("<button class='btn btn-default btn-box'>"+authName+" <span class='glyphicon glyphicon-remove'></span></button>");
					}
				});
	
		}else if($(ui.draggable).hasClass("group") && $(this).hasClass("groups")){
			var groupName = $(ui.draggable).children('#groupName').html();
			var groupId = $(ui.draggable).children('#groupId').html();	
			
			var obj = $(this);
			
			$.ajax({
	    		  	data: {"profile" : profile, "group": groupId},
	                url: "${addGroup}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.append("<button class='btn btn-default btn-box'>"+groupName+" <span class='glyphicon glyphicon-remove'></span></button>");
					}
				});
			
		}else if($(ui.draggable).hasClass("user") && $(this).hasClass("users")){
			var userName = $(ui.draggable).children('#userName').html();

			var obj = $(this);
			
			$.ajax({
	    		  	data: {"profile" : profile, "username": userName},
	                url: "${addMember}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.append("<button class='btn btn-default btn-box'>"+userName+" <span class='glyphicon glyphicon-remove'></span></button>");
					}
				});
			
		}else{
			return;
		}
	}
	
	function deleteAuth($profile, $profileName, $auth, $authName) {
	      
	      var $message = "Are you sure you want to remove '" + $authName + "' from '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Delete '" + $authName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"profile": $profile, "operation": $auth},
                url: "${removeAuth}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-profile-id="'+$profile+'"][data-auth-id="'+$auth+'"]').hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	function deleteGroup($profile, $profileName, $group, $groupName) {
	      
	      var $message = "Are you sure you want to remove '" + $groupName + "' from '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Delete '" + $groupName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"profile": $profile, "group": $group},
                url: "${removeGroup}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-profile-id="'+$profile+'"][data-group-id="'+$group+'"]').hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	function deleteUser($profile, $profileName, $user, $userName) {
	      
	      var $message = "Are you sure you want to remove '" + $userName + "' from '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Delete '" + $userName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"profile": $profile, "user": $user},
                url: "${removeMember}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-profile-id="'+$profile+'"][data-user-id="'+$user+'"]').hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	function deleteProfile($profile, $profileName) {
	      
	      var $message = "Are you sure you want to delete '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Delete '" + $profileName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"profile": $profile},
                url: "${delete}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('#'+$profile).hide().prev().hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	function copyProfile($profileTo, $profileFrom) {
	      
		 
	  };
	  


$(document).ready(function() {
	
	
//		new filter function
		$.extend( $.ui.autocomplete, {
			escapeRegex: function( value ) {
				return value.replace( /[\-\[\]{}()*+?.,\\\^$|#\s]/g, "\\$&" );
			},
			filter: function( array, term ) {
				var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex(term), "i");
	
				return $.grep( array, function( value ) {
					return matcher.test( value );
				} );
			}
		} );
		
		
		$("#userInp").autocomplete({
		    source: users,
		    minLength: 3,
		  });
	
	   $(".accordion").on("click", function(){

		   $(".accordion").not($(this)).removeClass("activeAccordion").next().css("display", "none");
		   
			$(this).toggleClass("activeAccordion");
		    var panel = $(this).next();
		    if (panel.css("display") === "block") {
		    	panel.css("display", "none");
		    } else {
		      panel.css("display", "block");
		    }
		
		});
	   
		$(".draggable_course").draggable({
			revert : 'invalid',
			helper: 'clone',
			appendTo: 'body',
			start: function() {
				$(this).addClass("course-dragging");
			}
		});
		
		$(".box").droppable({
			drop: dropFunction
		})

		$('#userForm').on('submit', function(){
		    $(this).after("<div class='draggable_course user ui-draggable'><div id='userName'>" + $(this).find("#userInp").val() + "</div></div>");
		    
		    $(this).parent().find(".user").draggable({
				revert : 'invalid',
				helper: 'clone',
				appendTo: 'body',
				start: function() {
					$(this).addClass("course-dragging");
				}
			});
		    
		    return false;
		 });
		
		$('#confirmDelete').on('show.bs.modal', function(e){ 
			
			var $profileId = $(e.relatedTarget).attr('data-profile-id');
			var $profileName = $(e.relatedTarget).attr('data-profile-name');
			var $type = $(e.relatedTarget).attr('data-type');
			
			if($type == "auth"){
				var $auth = $(e.relatedTarget).attr('data-auth-id');
				var $authName = $(e.relatedTarget).attr('data-auth-name');
				deleteAuth($profileId, $profileName, $auth, $authName);
			}else if($type == "group"){
				var $group = $(e.relatedTarget).attr('data-group-id');
				var $groupName = $(e.relatedTarget).attr('data-group-name');
				deleteGroup($profileId, $profileName, $group, $groupName);
			}else if($type == "user"){
				var $user = $(e.relatedTarget).attr('data-user-id');
				var $userName = $(e.relatedTarget).attr('data-user-name');
				deleteUser($profileId, $profileName, $user, $userName);
			}else{
				deleteProfile($profileId, $profileName);
			}

		});
		
		$('#copy').on('show.bs.modal', function(e){ 
			
			$profileTo = $(e.relatedTarget).attr('data-profile-id');
			
			
			$('#copy').find('.modal-footer #confirmCopy').on('click', function(){
			   	
				$profileFrom = $("#profileFrom").val();
				
				$(location).attr('href', "${copy}?profileTo="+$profileTo+"&profileFrom="+$profileFrom);
		      
		      $('#copy').not('.modal-footer #confirmCopy').on("click",function(){ 
		    	  $('#copy').find('.modal-footer #confirmCopy').off("click");	
		  	  });
			});  
			
			
		});
		
		 

})
</script>