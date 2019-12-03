<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="WORKLOGS"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

    <asset:stylesheet src="application.css"/>
    <asset:stylesheet src="custom.css"/>

    <asset:javascript src="jquery-2.2.0.min.js"/>

    <script src="https://cdn.dhtmlx.com/edge/dhtmlx.js?v=6.3.0"></script>
    <link rel="stylesheet" href="https://cdn.dhtmlx.com/edge/skins/terrace/dhtmlx.css?v=6.3.0">

    <asset:javascript src="dhtmlxgantt.js"/>
    <asset:javascript src="dhtmlxgantt_quick_info.js"/>
    <asset:javascript src="dhtmlxgantt_critical_path.js"/>
    <asset:javascript src="ext/dhtmlxgantt_marker.js"/>
    <asset:javascript src="ext/dhtmlxgantt_multiselect.js"/>
    <asset:javascript src="ext/dhtmlxgantt_undo.js"/>
    <asset:javascript src="ext/dhtmlxgantt_grouping.js"/>
    <asset:javascript src="ext/dhtmlxgantt_auto_scheduling.js"/>
    <asset:javascript src="ext/dhtmlxgantt_drag_timeline.js"/>
    <asset:javascript src="dhtmlxgantt_tooltip.js"/>
%{--    <asset:javascript src="testdata.js"/>--}%

    <asset:stylesheet src="dhtmlxgantt.css"/>

    <asset:stylesheet src="css/bootstrap-datepicker.min.css"/>
    <asset:javascript src="js/bootstrap-datepicker.min.js"/>

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
