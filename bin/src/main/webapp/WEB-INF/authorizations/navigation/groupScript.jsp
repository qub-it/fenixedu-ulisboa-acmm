
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<style>

#box {
	margin-top: 10px;
}

.btn-box{
	margin-bottom: 5px;
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


</style>


<script src="${pageContext.request.contextPath}/javaScript/jquery/jquery-ui.js"></script>



<spring:url var="addMenuToGroup" value="/navigation/addMenuToGroup"/>
<spring:url var="addUserToGroup" value="/navigation/addUserToGroup"/>
<spring:url var="removeMenuToGroup" value="/navigation/removeMenuToGroup"/>
<spring:url var="removeUserToGroup" value="/navigation/removeUserToGroup"/>


<script>

function dropFunction(event, ui) {
	if(!$(ui.draggable).hasClass("course-dragging"))
		return;
		
	if($(ui.draggable).hasClass("menu") && $(this).hasClass("menus")){
		var menuName = $(ui.draggable).children('#menuName').html();
		var menuId = $(ui.draggable).children('#menuId').html();
		var expression = $("#expression").html();
		
		var obj = $(this);
		$.ajax({
		  	data: {"menuItem": menuId, "expression": expression},
            url: "${addMenuToGroup}",
            type: 'POST',
            headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
            success: function(result) {
				obj.append("<button class='btn btn-default btn-box'>"+menuName+" <span class='glyphicon glyphicon-remove'></span></button>");
            }
		});

		
	}else if($(ui.draggable).hasClass("user") && $(this).hasClass("users")){
		var userName = $(ui.draggable).children('#userName').html();
		var expression = $("#expression").html();

		var obj = $(this);
		var obj = $(this);
		$.ajax({
		  	data: {"username": userName, "expression": expression},
            url: "${addUserToGroup}",
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


function deleteMenu($expression, $menuId, $menuName) {
    
    var $message = "Are you sure you want to remove '" + $menuName + "' ?";
    $('#confirmDelete').find('.modal-body p').text($message);
    var $title = "Remove '" + $menuName + "'";
    $('#confirmDelete').find('.modal-title').text($title);

    $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
  	  
  	  $.ajax({
  		  data: {"menuItem": $menuId , "expression": $expression},
          url: "${removeMenuToGroup}",
          type: 'POST',
          headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
          success: function(result) {
	          	$('#'+$menuId).hide();
	          	$('#confirmDelete').modal('hide');
			    }
			});
  	  
  	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
  	  
	  });
    
    $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
  	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  });
    
};

function deleteUser($expression, $userId, $userName) {
    
    var $message = "Are you sure you want to remove '" + $userName + "' ?";
    $('#confirmDelete').find('.modal-body p').text($message);
    var $title = "Remove '" + $userName + "'";
    $('#confirmDelete').find('.modal-title').text($title);

    $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
  	  
  	  $.ajax({
  		  data: {"username": $userName , "expression": $expression},
          url: "${removeUserToGroup}",
          type: 'POST',
          headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
          success: function(result) {
	          	$('#'+$userId).hide();
	          	$('#confirmDelete').modal('hide');
			    }
			});
  	  
  	  $('#confirmDelete').find('.modal-footer #confirm').off("click");
  	  
	  });
    
    $('#confirmDelete').not('.modal-footer #confirm').on("click",function(){ 
  	  $('#confirmDelete').find('.modal-footer #confirm').off("click");	
	  });
    
};


$(document).ready(function() {
//	new filter function
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

		var $expression = $("#expression").html();
		var $type = $(e.relatedTarget).attr('data-type');
		
		if($type == "menu"){
			var $menuId = $(e.relatedTarget).attr('data-menu-id');
			var $menuName = $(e.relatedTarget).attr('data-menu-name');
			deleteMenu($expression, $menuId, $menuName);
		}else if($type == "user"){
			var $userId = $(e.relatedTarget).attr('data-user-id');
			var $userName = $(e.relatedTarget).attr('data-user-name');
			deleteUser($expression, $userId, $userName);
		}

	});
	
})
</script>