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

    <asset:javascript src="js/kendo.all.min.js"/>
    <asset:stylesheet src="styles/kendo.common-material.min.css"/>
    <asset:stylesheet src="styles/kendo.material.min.css"/>

    <asset:stylesheet src="datatables.min.css"/>
    <asset:javascript src="datatables.min.js"/>

    <asset:stylesheet src="css/bootstrap-datepicker.min.css"/>
    <asset:javascript src="js/bootstrap-datepicker.min.js"/>

    <asset:stylesheet src="css/selectize.css"/>
    <asset:stylesheet src="css/selectize.bootstrap3.css"/>
    <asset:javascript src="js/standalone/selectize.min.js"/>

    <asset:stylesheet src="code/css/highcharts.css"/>
    <asset:javascript src="code/highcharts.js"/>
    <asset:javascript src="code/highcharts-more.js"/>
    <asset:javascript src="code/modules/heatmap.js"/>
    <asset:javascript src="code/modules/exporting.js"/>
    <asset:javascript src="code/modules/offline-exporting.js"/>
    <asset:javascript src="code/modules/export-data.js"/>

    %{--    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>--}%
    %{--    <script src="https://code.jquery.com/jquery-1.9.1.min.js"></script>--}%

    %{--<script crossorigin src="https://unpkg.com/react@16/umd/react.production.min.js"></script>--}%
    %{--<script crossorigin src="https://unpkg.com/react-dom@16/umd/react-dom.production.min.js"></script>--}%

    %{--<script crossorigin src="https://unpkg.com/react@16/umd/react.development.js"></script>--}%
    %{--<script crossorigin src="https://unpkg.com/react-dom@16/umd/react-dom.development.js"></script>--}%

%{--    <asset:javascript src="react.development.js"/>--}%
%{--    <asset:javascript src="react-dom.development.js"/>--}%

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
