<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<spring:url var="export" value="/export-import/export"/>
<spring:url var="importer" value="/export-import/import"/>



<div class="row">
<div class="col-md-5">
<a class="btn btn-default" role="button" href="${export}" download="exportedProfiles.json">
  Export
</a>

</div>

<div class="col-md-2"></div>

<div class="col-md-5">
<form action="${importer}" method="post" enctype="multipart/form-data">

<div class="form-group">
<label for="file">Ficheiro JSON</label>
<input name="file" id="file" type="file">
</div>

<button type="submit">Import</button>
</form>

</div>
</div>

