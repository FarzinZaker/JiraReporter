<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 11/26/2019
  Time: 8:47 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="gantt"/>
    <title>Planner</title>
</head>

<body>
<div id="content" role="main" class="container-fluid">
    <section class="row colset-2-its">
        <div class="col-lg-2">
            <g:render template="/filter/filter" model="${[hideTime: true]}"/>
        </div>

        <div class="col-lg-10">
            <g:render template="grid"/>
        </div>
    </section>
</div>

</body>
</html>