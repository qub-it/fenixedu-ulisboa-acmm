
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<style>

.headerProfile {
	font-weight: bold;
	font-size: 12px;
	color: #4b565c;
	padding: 8px;        
	border-bottom: 2px solid #ddd;
}



.draggable {
	padding: 5px;
	display: block;
	cursor: pointer;
}

.draggable:hover {
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



<spring:url var="addToProfile" value="/back-office-users/addToProfile"/>
<spring:url var="removeFromProfile" value="/back-office-users/removeFromProfile"/>
<script>

	function dropFunction(event, ui) {
		if(!$(ui.draggable).hasClass("dragging"))
			return;
		
		var user = $(this).attr("id");

		var profile = $(ui.draggable).children('#name').html();
		
		var obj = $(this)
		
		$.ajax({
    		  	data: {"profile" : profile, "user": user},
                url: "${addToProfile}",
                type: 'POST',
                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
                success: function(result) {
                	obj.append("<button class='btn btn-default btn-box'>"+profile+" <span class='glyphicon glyphicon-remove'></span></button>");
				}
			});
	}
	

	  
	function removeProfile($profileId, $profileName, $userId, $userName) {
	      
	      var $message = "Are you sure you want to remove '" + $profileName + "' from "+ $userName +" ?";
	      $('#confirmDelete').find('.modal-body p').text($message);
	      var $title = "Remove '" + $profileName + "'";
	      $('#confirmDelete').find('.modal-title').text($title);

	      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	  
	    	  $.ajax({
	    		  	data: {"profile": $profileName, "user": $userId},
	                url: "${removeFromProfile}",
	                type: 'POST',
	                headers: { '${csrf.headerName}' :  '${csrf.token}' } ,
	                success: function(result) {
	                	$('#'+$profileId).hide()
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
	
	   
		$(".draggable").draggable({
			revert : 'invalid',
			helper: 'clone',
			appendTo: 'body',
			start: function() {
				$(this).addClass("dragging");
			}
		});
		
		$(".box").droppable({
			drop: dropFunction
		});
		
		
		$('#confirmDelete').on('show.bs.modal', function(e){ 
			
			var $profileId = $(e.relatedTarget).attr('data-profile-id');
			var $profileName = $(e.relatedTarget).attr('data-profile');
			var $userId = $(e.relatedTarget).attr('data-user-id');
			var $userName = $(e.relatedTarget).attr('data-user');

			removeProfile($profileId, $profileName, $userId, $userName);

		});
		
		
		
		 

})
</script>