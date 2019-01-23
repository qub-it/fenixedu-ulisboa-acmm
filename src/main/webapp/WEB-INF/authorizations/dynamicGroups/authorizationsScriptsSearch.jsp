
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!--[if lt IE 9]>
	<script>
		document.createElement('header');
		document.createElement('section');
	</script>
<![endif]-->

<style>

/*	Filipe Varela / keoshi.com
 *  Joao Carvalho
 */

/* Typography */

.btn {
white-space: normal;
}

#authorizationList header {
	margin-top: 0;
	padding-top: 5px;
	display: block;
}

#cursos_acc.affix {
	top: 20px;
}

#cursos_acc {
	max-height: 90%;
	min-height: 400px;
	overflow-y: auto;
	min-width: 25%;
}

#container {
	font: 13px/1.6 'Helvetica Neue', Helvetica, Arial, sans-serif;
	color: #333;
}

h1 {
	font-size: 1.8em;
}

h2 {
	font-size: 1.4em;
}

h2 span {
	color: #888;
	padding-left: 10px;
}

/* General */
ul {
	list-style: none;
}

/* Structure */
#main {
	position: relative;
}

#all {
	position: relative;
	height: 100%;
	min-height: 600px;
}

/* Period */
#period {
	background: #f9f9f9;
	border: 1px solid #cccccc;
	margin-bottom: 32px;
	border-radius: 4px;
	box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.08), 0px 0px 0px 1px
		rgba(255, 255, 255, 0.6) inset;
}

#period header {
	padding: 15px 14px;
	border-bottom: 1px solid #cccccc;
	border-top-left-radius: 4px;
	border-top-right-radius: 4px;
	cursor: pointer;
	text-shadow: 0px 1px 0px rgba(255, 255, 255, 0.5);
	box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.08), 0px 1px 3px
		rgba(255, 255, 255, 0.6) inset;
}

#period header h2 span {
	padding-left: 10px;
}

#period header span {
	color: #666;
	font-size: 13px;
	font-weight: normal;
}

#period header a.edit-auth {
	color: #848484;
	font-size: 12px;
	font-weight: bold;
	float: right !important;
	margin-top: -20px;
	text-decoration: none;
	border-bottom: none;
}

#period header a.edit-auth .symbol {
	font-size: 2em;
	color: #848484;
	float: left;
	margin: -12px 4px 0 0;
	vertical-align: -2px;
}

#period header a:hover.edit-auth,
#period header a:hover.edit-auth .symbol
	{
	color: #333;
}

#period ul li {
	border-top: 1px solid #e6e6e6;
	padding: 6px 20px 6px 16px;
	overflow: hidden;
}

#period ul li:first-child {
	border: none;
}

#period ul li span {
	color: #999;
}

#period ul li img, #period ul li input {
	float: right;
	display: inline-block;
	cursor: pointer;
}

#period .placeholder-tip span {
	 vertical-align: middle;
	 color: black;
}

ul.courses-list {
	padding-left: 0px;
}

/* Periods Selection */


/* Header/Selection colors */
.authorization header {
	background:#d7e38c;
	background-image: linear-gradient(bottom, rgb(215,227,140) 100%, rgb(202,215,127) 0%);
	background-image: -o-linear-gradient(bottom, rgb(215,227,140) 100%, rgb(202,215,127) 0%);
	background-image: -moz-linear-gradient(bottom, rgb(215,227,140) 100%, rgb(202,215,127) 0%);
	background-image: -webkit-linear-gradient(bottom, rgb(215,227,140) 100%, rgb(202,215,127) 0%);
	background-image: -ms-linear-gradient(bottom, rgb(215,227,140) 100%, rgb(202,215,127) 0%);

	background-image: -webkit-gradient(
		linear,
		left bottom,
		left top,
		color-stop(1, rgb(215,227,140)),
		color-stop(0, rgb(202,215,127))
	);
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#d7e38c', endColorstr='#cad77f',GradientType=0 );
}

.unit header {
	background:#f1d67e;
	background-image: linear-gradient(bottom, rgb(254,225,139) 100%, rgb(241,214,126) 0%);
	background-image: -o-linear-gradient(bottom, rgb(254,225,139) 100%, rgb(241,214,126) 0%);
	background-image: -moz-linear-gradient(bottom, rgb(254,225,139) 100%, rgb(241,214,126) 0%);
	background-image: -webkit-linear-gradient(bottom, rgb(254,225,139) 100%, rgb(241,214,126) 0%);
	background-image: -ms-linear-gradient(bottom, rgb(254,225,139) 100%, rgb(241,214,126) 0%);

	background-image: -webkit-gradient(
		linear,
		left bottom,
		left top,
		color-stop(1, rgb(254,225,139)),
		color-stop(0, rgb(241,214,126))
	);
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#fee18b', endColorstr='#f1d67e',GradientType=0 );
}

/* Edit Periods */
.authorization-edit {
	padding: 30px 0 30px 30px;
	border-bottom: 1px solid #cccccc;
}

.authorization-edit fieldset {
	margin-bottom: 15px;
}

.authorization-edit fieldset legend {
	display: none;
}

.edit-authorizations label {
	display: block;
	font-weight: bold;
	margin-bottom: 5px;
}

.edit-authorizations input {
	font-size: 0.9em;
	color: #666;
	margin: 0px;
	padding: 6px 6px;
	border: 1px solid #d6d6d6;
	outline: none;
	box-shadow: 0px 0px 6px rgba(255, 255, 255, 0), 0px 1px 2px
		rgba(0, 0, 0, 0.1) inset;
	border-radius: 4px;
}

.edit-authorizations input:disabled.confirmar,
.edit-authorizations input:disabled.confirmar:hover,
.edit-authorizations input:disabled.confirmar:focus,
.edit-authorizations input:disabled.confirmar:active {
	background:#c9d7e5;
	box-shadow:none;
}


.edit-authorizations input:focus {
	color: #333;
	border: 1px solid rgb(158, 198, 222);
	box-shadow: 0px 0px 6px rgba(158, 198, 222, 0.6), 0px 1px 2px
		rgba(0, 0, 0, 0.2) inset;
}

.edit-authorizations select {
	font-size: 0.9em;
	color: #666;
	margin: 0px;
	border: 1px solid #d6d6d6;
	outline: none;
}

.edit-authorizations select:focus {
	color: #333;
	border: 1px solid rgb(158, 198, 222);
	box-shadow: 0px 0px 6px rgba(158, 198, 222, 0.6), 0px 1px 2px
		rgba(0, 0, 0, 0.2) inset;
}

.authorization-edit .data-bloco {
	margin-right: 20px;
	display: inline-block;
}

.authorization-edit .links-authorization {
	font-size: 0.85em;
	float: right;
	margin-right: 30px;
	margin-top: -10px;
}

.authorization-edit .symbol {
	color: #999;
	font-size: 2.2em;
	line-height: 0.1em;
	vertical-align: -4px;
	margin-left: 6px;
}

.authorization-edit .symbol a {
	color: #999;
	text-decoration: none;
}

.authorization-edit .symbol a:hover {
	color: #105c93;
}

.edit-authorizations input.confirmar,.edit-authorizations input.cancelar {
	margin-top: 10px;
	font: 1em sans-serif;
	padding: 8px 20px;
	color: white;
	border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.2) rgba(0, 0, 0, 0.4);
	cursor: pointer;
	text-shadow: 0px -1px 0px rgba(0, 0, 0, 0.3);
	box-shadow: 0px 0px 6px rgba(255, 255, 255, 0.2) inset, 0px 1px 4px
		rgba(0, 0, 0, 0.3);
}

.edit-authorizations input.confirmar {
	background-color: rgb(16, 92, 147);
	background-image: -webkit-linear-gradient(top, rgb(142, 174, 202),
		rgb(101, 138, 175) );
}

.edit-authorizations input.cancelar {
	margin-left: 15px;
	background-color: rgb(16, 92, 147);
	background-image: -webkit-linear-gradient(top, rgb(210, 210, 210),
		rgb(150, 150, 150) );
}

.edit-authorizations input.confirmar:hover,
.edit-authorizations input.confirmar:focus,
.edit-authorizations input.cancelar:hover,
.edit-authorizations input.cancelar:focus
	{
	box-shadow: 0px 0px 10px rgba(255, 255, 255, 0.8) inset, 0px 1px 4px
		rgba(0, 0, 0, 0.3);
}

.edit-authorizations input.confirmar:active,.edit-authorizations input.cancelar:active
	{
	box-shadow: 0px 1px 4px 1px rgba(0, 0, 0, 0.5) inset, 0px 1px 4px
		rgba(0, 0, 0, 0.3);
}

.links-authorization a.eliminar {
	color: #cc3333;
	margin-left: 10px;
}

/* Sidebar */

#sidebar {
	background: #fafafa;
	border: 1px solid #cccccc;
	margin-bottom: 32px;
	border-radius: 4px;
	box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.08), 0px 0px 0px 1px
		rgba(255, 255, 255, 0.6) inset;
}

#sidebar h3 {
	width: 100%;
	color: #666;
	font-size: 1.1em;
	font-weight: bold;
	text-decoration: none;
	display: inline-block;
	padding: 8px 0;
	text-indent: 5px;
	border-top: 1px solid #ccc;
	background-image: linear-gradient(bottom, rgb(234, 234, 234) 100%,
		rgb(221, 221, 221) 0% );
	background-image: -o-linear-gradient(bottom, rgb(234, 234, 234) 100%,
		rgb(221, 221, 221) 0% );
	background-image: -moz-linear-gradient(bottom, rgb(234, 234, 234) 100%,
		rgb(221, 221, 221) 0% );
	background-image: -webkit-linear-gradient(bottom, rgb(234, 234, 234)
		100%, rgb(221, 221, 221) 0% );
	background-image: -ms-linear-gradient(bottom, rgb(234, 234, 234) 100%,
		rgb(221, 221, 221) 0% );
	background-image: -webkit-gradient(linear, left bottom, left top, color-stop(1, rgb(234,
		234, 234) ), color-stop(0, rgb(221, 221, 221) ) );
	text-shadow: 0px 1px 0px rgba(255, 255, 255, 0.5);
	box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.08), 0px 1px 1px 0px
		rgba(255, 255, 255, 0.6) inset;
}

#sidebar h3:first-child {
	border-bottom: 1px solid #ccc;
	border-top-left-radius: 4px;
	border-top-right-radius: 4px;
}

#sidebar h3:last-child {
	border-bottom-left-radius: 4px;
	border-bottom-right-radius: 4px;
}

#sidebar ul {
	margin: 10px 0 16px;
	text-indent: 14px;
	line-height: 24px;
}

#sidebar li {
	display: block;
	cursor: pointer;
}

#sidebar li:hover {
	background: rgba(255, 180, 23, 0.2);
}

.draggable_course {
	padding: 5px;
	display: block;
	cursor: pointer;
}

.draggable_course:hover {
	background: rgba(255, 180, 23, 0.2);
}

/* Misc */
.authorization-edit,.hide {
	display: none;
}

/* Transitions */
a,input,.symbol {
	-webkit-transition: all 0.15s ease;
}

.separator {
	border-left: 1px solid #E6E6E6;
}

.inactive {
	opacity:0.3;
}

#warning {
  display: none;
}

.alert {
  padding: 20px;
  background-color: #d22317f2;
  color: white;
  border-radius: 5px;
}

</style>

<spring:url var="revokeGroup" value="/dynamic-groups/revoke"/>
<spring:url var="addGroup" value="/dynamic-groups/add"/>

<script src="${pageContext.request.contextPath}/javaScript/jquery/jquery-ui.js"></script>

<script>
	
	function revokeGroup($userName, $userId, $groupName, $groupId) {
	      
	      var $message = "Are you sure you want to remove '" + $userName + "' from '" + $groupName + "' ?";
	      $('#confirmRevokeGroup').find('.modal-body p').text($message);
	      var $title = "Remove '" + $groupName + "'";
	      $('#confirmRevokeGroup').find('.modal-title').text($title);

	      $('#confirmRevokeGroup').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  data: {"user" : $userId, "group": $groupId},
                  url: "${revokeGroup}",
                  type: 'POST',
                  headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                  success: function(result) {
                	  $('#'+$groupId).hide();
                	  $('#confirmRevokeGroup').modal('hide');
				    }
				});
	    	  
	    	  $('#confirmRevokeGroup').find('.modal-footer #confirm').off("click");
	    	  
		  });
	      
	      $('#confirmRevokeGroup').not('.modal-footer #confirm').on("click",function(){ 
	    	  $('#confirmRevokeGroup').find('.modal-footer #confirm').off("click");	
	  	  });
	      
	  };
	  
	  	
	

	function dropFunction(event, ui) {
		if(!$(ui.draggable).hasClass("course-dragging"))
			return;
		
		var userId = $(this).parent().parent().attr('id');
		
		var obj = $(this);

		var groupName = $(ui.draggable).children('#groupName').html();
		var groupId = $(ui.draggable).children('#groupId').html();
			
		$.ajax({
    		  data: {"group": groupId, "user": userId},
              url: "${addGroup}",
              type: 'POST',
              dataType: 'json',
              headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
              success: function(result) {
            	  	  
           	    var dropbl = $(obj).parent().prepend('<tr><td><button style="margin-bottom: 2px;" data-group-expression="'+groupName+'" data-user-name="${user.username}" data-group-id="'+groupId+'" data-url="${revokeGroup}" data-user-id="'+userId+'" data-toggle="modal" data-target="#confirmRevokeGroup" class="btn btn-default"  title=<spring:message code="label.delete"/>>'+groupName+' <span class="glyphicon glyphicon-remove"></span></button></td></tr>');
              		
           		dropbl.parent().find("#"+result).droppable();
           		dropbl.parent().find("#"+result).droppable("enable");

           		$('.auth').droppable({
					drop: dropFunction
				})
              		
              }
			});
				
	
	}
	
	
	
	
	
	$(document).ready(
			function() {
				
// 				new filter function
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

				
				$("#userInp2").autocomplete({
				    source: users,
				    minLength: 3,
				  });

				$('.auth').droppable({
					drop: dropFunction
				});

				$(".draggable_course").draggable({
					revert : 'invalid',
					helper: 'clone',
					appendTo: 'body',
					start: function() {
						$(this).addClass("course-dragging");
					}
				});
				

				$('#confirmRevokeGroup').on('show.bs.modal', function(e){ 
								
					var $userName = $(e.relatedTarget).attr('data-user-name');
					var $userId = $(e.relatedTarget).attr('data-user-id');
				    var $groupName = $(e.relatedTarget).attr('data-group-expression');
				    var $groupId = $(e.relatedTarget).attr('data-group-id');
					
				    revokeGroup($userName, $userId, $groupName, $groupId);
					    
					
				});

			});

</script>