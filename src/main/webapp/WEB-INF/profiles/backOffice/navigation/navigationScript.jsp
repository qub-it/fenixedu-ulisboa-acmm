
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



<spring:url var="getTree" value="/back-office-navigationProfile/getTree"/>


<script>

function loadTree($operation){
	  $(".tree").fancytree({
			source: {
				url: "${getTree}?operation="+$operation,
			}
		});
	  
};

</script>