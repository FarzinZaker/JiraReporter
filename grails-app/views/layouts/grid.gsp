<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="PLANNER"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="custom.css"/>

    <asset:javascript src="jquery-2.2.0.min.js"/>

    <asset:javascript src="js/kendo.all.min.js"/>
    <asset:stylesheet src="styles/kendo.material-v2.min.css"/>

    <asset:stylesheet src="css/bootstrap-datepicker.min.css"/>
%{--    <asset:javascript src="js/bootstrap-datepicker.min.js"/>--}%

    <asset:stylesheet src="css/selectize.css"/>
    <asset:stylesheet src="css/selectize.bootstrap3.css"/>
    <asset:javascript src="js/standalone/selectize.min.js"/>

    <g:layoutHead/>
</head>

<body>

<div class="row">
    <div class="header">
        <span class="logo">
            <asset:image src="aclate.png"/>
            <span>
                ${g.layoutTitle()?.toString()?.toUpperCase()}
            </span>
        </span>
    </div>
</div>

<div class="navbar navbar-default navbar-static-top" role="navigation">
</div>

<g:layoutBody/>

<div class="footer" role="contentinfo"></div>

<div id="spinner" class="spinner" style="display:none;">
    <g:message code="spinner.alt" default="Loading&hellip;"/>
</div>

%{--<asset:javascript src="application.js"/>--}%
<asset:javascript src="bootstrap.js"/>

</body>
</html>
