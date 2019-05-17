
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<style>

.scrollable{
	overflow-y: scroll;
    max-height: 450px;
}

.box > .btn {
    margin: 5px;
}

.authtip {
  position: relative;
  display: inline-block;
/*   border-bottom: 1px dotted black; */
}

.authtip .authtiptext {
  visibility: hidden;
/*   width: 120px; */
  background-color: #555;
  color: #fff;
/*   text-align: center; */
  border-radius: 6px;
  padding: 5px 0;
  position: absolute;
  z-index: 1;
  bottom: 125%;
/*   left: 50%; */
  left: 25%;
  margin-left: -60px;
  opacity: 0;
  transition: opacity 0.3s;
}

.authtip .authtiptext::after {
  content: "";
  position: absolute;
  top: 100%;
  left: 50%;
  margin-left: -5px;
  border-width: 5px;
  border-style: solid;
  border-color: #555 transparent transparent transparent;
}

.authtip:hover .authtiptext {
  visibility: visible;
  opacity: 1;
}






.headerProfile {
	font-weight: bold;
	font-size: 12px;
	color: #4b565c;
	padding: 8px;        
	border-bottom: 2px solid #ddd;
}

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
  background-image: -webkit-gradient( linear, left bottom, left top, color-stop(1, rgb(245, 245, 245)), color-stop(0, rgb(241, 241, 241)) );
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
  background-image: -webkit-gradient( linear, left bottom, left top, color-stop(1, rgb(241, 241, 241)), color-stop(0, rgb(162, 162, 162)) );
}

.accordion-panel {
  padding: 15px;
  display: none;
  background-color: white;
  overflow: hidden;
}
</style>


<script src="${pageContext.request.contextPath}/javaScript/jquery/jquery-ui.js"></script>


<spring:url var="addAuth" value="/back-office-profiles/addAuth"/>
<spring:url var="removeAuth" value="/back-office-profiles/removeAuth"/>
<spring:url var="addMember" value="/back-office-profiles/addMember"/>
<spring:url var="removeMember" value="/back-office-profiles/removeMember"/>
<spring:url var="addToMenu" value="/back-office-profiles/addToMenu"/>
<spring:url var="removeFromMenu" value="/back-office-profiles/removeFromMenu"/>
<spring:url var="delete" value="/back-office-profiles/delete"/>
<spring:url var="copy" value="/back-office-profiles/copy"/>
<spring:url var="modifyOffice" value="/back-office-profiles/modifyOffice"/>
<spring:url var="modifyProgram" value="/back-office-profiles/modifyProgram"/>
<spring:url var="addChild" value="/back-office-profiles/addChild"/>
<spring:url var="removeChild" value="/back-office-profiles/removeChild"/>
<spring:url var="getTree" value="/back-office-profiles/getTree"/>
<spring:url var="getMenusTree" value="/back-office-profiles/getMenusTree"/>






<script>

	function dropFunction(event, ui) {
		if(!$(ui.draggable).hasClass("course-dragging"))
			return;
		
		var profile = $(this).parent().attr("id");
		
		
		
		if($(ui.draggable).hasClass("authorization") && $(this).hasClass("table")){
			var authName = $(ui.draggable).children('#presentationName').html();
			var operation = $(ui.draggable).children('#operationName').html();
			
			var obj = $(this);
			
			$.ajax({
	    		  	data: {"profile" : profile, "operation": operation},
	                url: "${addAuth}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.append("<tr class='authorizations ui-droppable' id='"+result+"'><td><button class='btn btn-default btn-box' data-profile-id='"+profile+"' data-auth-id='"+result+"' data-type='auth' data-toggle='modal' data-target='#confirmDelete' >"+authName+" <span class='glyphicon glyphicon-remove'></span></button></td><td><table class='office-list'></table> </td> <td><table class='program-list'></table></td></tr>");

	                	$(".authorizations").droppable({
	            			drop: dropFunction
	            		})

	                
	                }
				});
			
		}else if($(ui.draggable).hasClass("office") && $(this).hasClass("authorizations")){
			var officeName = $(ui.draggable).children('#presentationName').html();
			var office = $(ui.draggable).children('#oid').html();
			
			var obj = $(this);
			
			$.ajax({
					data: {"rule": obj.attr("id"), "scope":office, "action":"add"},
	                url: "${modifyOffice}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.find(".office-list").append("<tr><td><button class='btn btn-default btn-box' data-office-id='"+office+"' data-office-name='"+officeName+"' data-auth-id='"+obj.attr("id")+"' data-auth-name='"+authName+"' data-type='office' data-toggle='modal' data-target='#confirmDelete' >"+officeName+" <span class='glyphicon glyphicon-remove'></span></button></td></tr>");
						}
				});
			
		}else if($(ui.draggable).hasClass("program") && $(this).hasClass("authorizations")){
			var programName = $(ui.draggable).children('#presentationName').html();
			var program = $(ui.draggable).children('#oid').html();
			
			var obj = $(this);
			
			$.ajax({
					data: {"rule": obj.attr("id"), "scope":program, "action":"add"},
	                url: "${modifyProgram}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.find(".program-list").append("<tr><td><button class='btn btn-default btn-box' data-program-id='"+program+"' data-program-name='"+programName+"' data-auth-id='"+obj.attr("id")+"' data-auth-name='"+authName+"' data-type='program' data-toggle='modal' data-target='#confirmDelete' >"+programName+" <span class='glyphicon glyphicon-remove'></span></button></td></tr>");
						}
				});
			
		}else if($(ui.draggable).hasClass("menu") && $(this).hasClass("menus")){
			var menuPath = $(ui.draggable).children('#path').html();
			var menu = $(ui.draggable).children('#oid').html();

			var obj = $(this);
			
			$.ajax({
	    		  	data: {"profile" : profile, "menu": menu},
	                url: "${addToMenu}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	loadTree(profile,$("#"+profile).prev().html());
// 	                	obj.append("<button  data-profile-id='"+profile+"' data-menu-id='"+result+"' class='btn btn-default btn-box' data-type='menu' data-toggle='modal' data-target='#confirmDelete' >"+menuPath+" <span class='glyphicon glyphicon-remove'></span></button>");
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
	                	obj.append("<button  data-profile-id='"+profile+"' data-user-id='"+result+"' class='btn btn-default btn-box' data-type='user' data-toggle='modal' data-target='#confirmDelete' >"+userName+" <span class='glyphicon glyphicon-remove'></span></button>");
					}
				});
			
		}else if($(ui.draggable).hasClass("profile") && $(this).hasClass("subprofiles")){
			var childName = $(ui.draggable).children('#name').html();
			var child = $(ui.draggable).children('#oid').html();
			
			var obj = $(this);
			
			$.ajax({
	    		  	data: {"parent" : profile, "child": child},
	                url: "${addChild}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	obj.append("<button data-profile-id='"+profile+"' data-child-id='"+child+"' class='btn btn-default btn-box' data-type='child' data-toggle='modal' data-target='#confirmDelete' >"+childName+" <span class='glyphicon glyphicon-remove'></span></button>");
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
	    		  data: {"profile": $profile, "rule": $auth},
                url: "${removeAuth}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-profile-id="'+$profile+'"][data-auth-id="'+$auth+'"]').parent().parent().parent().hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	  
	function deleteOffice($office, $officeName, $auth, $authName) {
	      
	      var $message = "Are you sure you want to remove '" + $officeName + "' from '" + $authName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Remove '" + $officeName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"rule": $auth, "scope":$office, "action":"remove"},
                url: "${modifyOffice}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-office-id="'+$office+'"][data-auth-id="'+$auth+'"]').hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	  
	function deleteProgram($program, $programName, $auth, $authName) {
	      
	      var $message = "Are you sure you want to remove '" + $programName + "' from '" + $authName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Remove '" + $programName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"rule": $auth, "scope":$program, "action":"remove"},
                url: "${modifyProgram}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-program-id="'+$program+'"][data-auth-id="'+$auth+'"]').hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	  
	  
	function deleteMenu($profileId, $profileName, $menu, $menuPath) {
	      
	      var $message = "Are you sure you want to remove '" + $menuPath + "' from '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Remove '" + $menuPath + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"profile": $profileId, "menu": $menu},
                url: "${removeFromMenu}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	loadTree($profileId,$profileName);
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	function deleteUser($profileId, $profileName, $user, $userName) {
	      
	      var $message = "Are you sure you want to remove '" + $userName + "' from '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Remove '" + $userName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"profile": $profileId, "user": $user},
                url: "${removeMember}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-profile-id="'+$profileId+'"][data-user-id="'+$user+'"]').hide();
                	$('#confirmDelete').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	  
	function deleteChild($profileId, $profileName, $child, $childName) {
	      
	      var $message = "Are you sure you want to remove child '" + $childName + "' from '" + $profileName + "' ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Remove '" + $childName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"parent": $profileId, "child": $child},
                url: "${removeChild}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	$('button[data-profile-id="'+$profileId+'"][data-child-id="'+$child+'"]').hide();
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
				    },
					error: function(){
						$('#confirmDelete').modal('hide');
						$('#childNotification').modal('show');
						$('#childNotification').find('.modal-body p').text('Please remove this profile from all menus before deleting it');
					    $('#childNotification').find('.modal-title').text('Error on delete');
					}
				});
	    	  
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	  function loadTree($profile, $profileName){
		  $(".tree"+$profile).fancytree({
				source: {
					url: "${getTree}?profile="+$profile,
				},
				dblclick: function(event, data) {
					
					$('#confirmDelete').modal("show");
					
					deleteMenu($profile, $profileName, data.node.key, data.node.title);
									
				},
			});
		  $(".draggable_course").draggable({
				revert : 'invalid',
				helper: 'clone',
				appendTo: 'body',
				start: function() {
					$(this).addClass("course-dragging");
					if($(this).hasClass("authorization") || $(this).hasClass("office") || $(this).hasClass("program")){
						$(".table-auths").effect( "highlight", 3000 );
					}else if($(this).hasClass("menu")){
						$(".menus").effect( "highlight", 3000 );
					}else if($(this).hasClass("menu")){
						$(".menus").effect( "highlight", 3000 );
					}else if($(this).hasClass("profile")){
						$(".subprofiles").effect( "highlight", 3000 );
					} else if($(this).hasClass("user")){
						$(".users").effect( "highlight", 3000 );
					} 
				}
			});
	  };
	  
	  
	  
		  
		  function closeAllLists() {
		    $(".user").remove();
		  }		
	 
	  

$(document).ready(function() {
	
	$("#menus").fancytree({
		source: {
			url: "${getMenusTree}",
		}
	});
		
//		new filter function
		$.extend( $.ui.autocomplete, {
			escapeRegex: function( value ) {
				return value.replace( /[\-\[\]{}()*+?.,\\\^$|#\s]/g, "\\$&" );
			},
			filter: function( array, term ) {
// 				var matcher = new RegExp("^" + $.ui.autocomplete.escapeRegex(term), "i");
				var matcher = new RegExp( $.ui.autocomplete.escapeRegex( "[ A-Z-@.()]*" + term.split(" ").join("[ A-Z-@.()]*") + "[ A-Z-@.()]*" ), "gi" );
				return $.grep( array, function( value ) {
					return matcher.test( value );
				} );
			}
		} );
		
		
		$("#userInp").autocomplete({
		    source: users,
		    minLength: 3,

		  });
		
		$(".groupInp").autocomplete({
		    source: profiles,
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
				if($(this).hasClass("authorization") || $(this).hasClass("office") || $(this).hasClass("program")){
					$(".table-auths").effect( "highlight", 3000 );
				}else if($(this).hasClass("menu")){
					$(".menus").effect( "highlight", 3000 );
				}else if($(this).hasClass("menu")){
					$(".menus").effect( "highlight", 3000 );
				}else if($(this).hasClass("profile")){
					$(".subprofiles").effect( "highlight", 3000 );
				} else if($(this).hasClass("user")){
					$(".users").effect( "highlight", 3000 );
				} 
			}
		});
		
		
		$(".box").droppable({
			drop: dropFunction
		});
		
		$(".table-auths").droppable({
			drop: dropFunction
		})
		
		$(".authorizations").droppable({
			drop: dropFunction
		})
		
		
		$("#filter-users").find("input").keyup(function(e) {
			arr = users;
			
		      var a, b, i, val = this.value;

	    	  closeAllLists();
		      if (!val || val.length < 4) {
		    	  return false;
		      }
		      for (i = 0; i < arr.length; i++) {
		    	  
					var matcher = new RegExp("[ A-Z-@.()]*" + val.toLowerCase().split(" ").join("[ A-Z-@.()]*") + "[ A-Z-@.()]*", "gi" );
		    	  
		        if (matcher.test(arr[i].toLowerCase())) {
		        	$("#usersResults").append("<div class='draggable_course user ui-draggable'><div id='userName'>" + arr[i] + "</div></div>");
		        
		        	$("#usersResults").find(".user").draggable({
						revert : 'invalid',
						helper: 'clone',
						appendTo: 'body',
						start: function() {
							$(this).addClass("course-dragging");

							if($(this).hasClass("authorization") || $(this).hasClass("office") || $(this).hasClass("program")){
								$(".table-auths").effect( "highlight", 3000 );
							}else if($(this).hasClass("menu")){
								$(".menus").effect( "highlight", 3000 );
							}else if($(this).hasClass("menu")){
								$(".menus").effect( "highlight", 3000 );
							}else if($(this).hasClass("profile")){
								$(".subprofiles").effect( "highlight", 3000 );
							} else if($(this).hasClass("user")){
								$(".users").effect( "highlight", 3000 );
							} 
						}
					});
		        
		        }
		      }
		  });

		
		$('#confirmDelete').on('show.bs.modal', function(e){ 
			
			var $profileId = $(e.relatedTarget).attr('data-profile-id');
			var $profileName = $(e.relatedTarget).attr('data-profile-name');
			var $type = $(e.relatedTarget).attr('data-type');
			
			if($type == "auth"){
				var $auth = $(e.relatedTarget).attr('data-auth-id');
				var $authName = $(e.relatedTarget).attr('data-auth-name');
				deleteAuth($profileId, $profileName, $auth, $authName);
			}else if($type == "office"){
				var $office = $(e.relatedTarget).attr('data-office-id');
				var $officeName = $(e.relatedTarget).attr('data-office-name');
				var $auth = $(e.relatedTarget).attr('data-auth-id');
				var $authName = $(e.relatedTarget).attr('data-auth-name');
				deleteOffice($office, $officeName, $auth, $authName);
			}else if($type == "program"){
				var $program = $(e.relatedTarget).attr('data-program-id');
				var $programName = $(e.relatedTarget).attr('data-program-name');
				var $auth = $(e.relatedTarget).attr('data-auth-id');
				var $authName = $(e.relatedTarget).attr('data-auth-name');
				deleteProgram($program, $programName, $auth, $authName);
			}else if($type == "user"){
				var $user = $(e.relatedTarget).attr('data-user-id');
				var $userName = $(e.relatedTarget).attr('data-user-name');
				deleteUser($profileId, $profileName, $user, $userName);
			}else if($type == "child"){
				var $child = $(e.relatedTarget).attr('data-child-id');
				var $childName = $(e.relatedTarget).attr('data-child-name');
				deleteChild($profileId, $profileName, $child, $childName);
			}else if($type == "profile"){
				deleteProfile($profileId, $profileName);
			}

		});
		
		
		
		 

})
</script>