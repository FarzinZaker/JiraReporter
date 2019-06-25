<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Worklog Report</title>

    <style>
    .worklog {
        border: 1px solid #dddddd;
        border-radius: 5px;
        background: white;
        padding: 10px;
        margin-bottom: 15px;
        /*box-shadow: 2px 2px 5px gray;*/
        margin-top: 45px;
    }

    table {
        border-width: 0;
    }

    table td {
        border-bottom: 1px solid #eeeeee;
    }

    .worklog .user {
        margin-top: -35px;
        margin-bottom: 10px;
    }

    .worklog .user div {
        background: white;
        display: inline-block;
        border-radius: 24px;
        border: 1px solid #dddddd;
    }

    .user a {
        display: inline-block;
        margin-right: 24px;
        margin-left: 10px;
        text-decoration: none;
    }

    .user.center a {
        display: block;
        text-align: center;
        margin: 0;
    }

    .user img {
        width: 48px;
        height: 48px;
        border-radius: 24px;
    }

    table td.other {
        line-height: 48px;
    }

    .user.center {
        text-align: center;
        padding-top: 20px;
    }

    .started {
        font-size: 12px;
        display: inline-block;
    }

    .worklog .timeSpent {
        font-weight: bold;
        display: inline-block;
        background: #eeeeee;
        padding-left: 5px;
        padding-right: 5px;
        margin-right: 10px;
    }

    table .timeSpent {
        font-weight: bold;
        line-height: 48px;
        text-align: right
    }

    table .timeSpent.center {
        text-align: center;
    }

    .comment {
        padding: 10px;
    }

    .issue {
        background: #f4f4f4;
        margin: -10px;
        margin-top: 10px;
        border-radius: 0 0 5px 5px;
        padding: 5px;
        padding-left: 10px;
        font-size: 12px;
        font-weight: bold;
    }

    .issue a {
        text-decoration: none;
    }

    .nav-tabs {
        margin-top: 20px;
    }

    * {
        text-shadow: none !important;
    }

    .tag {
        display: inline-block;
        background: white;
        padding: 2px 7px 2px 5px;
        border-radius: 5px;
        margin-left: 8px;
        margin-right: 8px;
    }

    hr {
        margin-top: 5px;
        margin-bottom: 5px;
    }

    tbody td, thead td {
        white-space: nowrap;
        /*padding: 0 !important;*/
    }

    .row {
        padding: 0;
    }

    .DTFC_LeftBodyLiner {
        overflow-x: hidden;
    }

    thead tr td, thead tr td *, thead tr td *:hover, .dataTables_scrollHead, .dataTables_scrollHead * {
        color: white;
        vertical-align: bottom;
    }

    .dataTables_wrapper{
        text-align: right;
    }

    .dataTables_wrapper .dataTables_filter {
        padding-top: 12px;
        padding-bottom: 10px;
        margin-right: 285px;
    }
    .dataTables_wrapper .btn-group {
        padding-top: 10px;
        margin-bottom:-70px;
    }

    .dataTables_wrapper .row + .row {
        padding: 0;
    }

    .dataTables_wrapper .row + .row + .row {
        padding-top: 10px;
        padding-bottom: 10px;
        margin-right: 15px;
        margin-left: 15px;
        margin-bottom: 30px;
    }

    .dataTables_wrapper .dataTables_info {
        padding: 5px;
        padding-right: 20px;
    }

    h2 {
        margin-top: 60px;
        margin-bottom: -55px;
    }

    #DataTables_Table_0_wrapper thead tr td,
    #DataTables_Table_0_wrapper thead tr td *,
    #DataTables_Table_0_wrapper thead tr td *:hover,
    #DataTables_Table_0_wrapper .dataTables_scrollHead,
    #DataTables_Table_0_wrapper .dataTables_scrollHead * {
        background-color: #673AB7;
    }

    #DataTables_Table_0_wrapper .dataTables_info {
        background: #D1C4E9;
    }

    #DataTables_Table_1_wrapper thead tr td,
    #DataTables_Table_1_wrapper thead tr td *,
    #DataTables_Table_1_wrapper thead tr td *:hover,
    #DataTables_Table_1_wrapper .dataTables_scrollHead,
    #DataTables_Table_1_wrapper .dataTables_scrollHead * {
        background-color: #009688;
    }

    #DataTables_Table_1_wrapper .dataTables_info {
        background: #B2DFDB;
    }

    #DataTables_Table_2_wrapper thead tr td,
    #DataTables_Table_2_wrapper thead tr td *,
    #DataTables_Table_2_wrapper thead tr td *:hover,
    #DataTables_Table_2_wrapper .dataTables_scrollHead,
    #DataTables_Table_2_wrapper .dataTables_scrollHead * {
        background-color: #3F51B5;
    }

    #DataTables_Table_2_wrapper .dataTables_info {
        background: #C5CAE9;
    }

    #DataTables_Table_3_wrapper thead tr td,
    #DataTables_Table_3_wrapper thead tr td *,
    #DataTables_Table_3_wrapper thead tr td *:hover,
    #DataTables_Table_3_wrapper .dataTables_scrollHead,
    #DataTables_Table_3_wrapper .dataTables_scrollHead * {
        background-color: #E91E63;
    }

    #DataTables_Table_3_wrapper .dataTables_info {
        background: #F8BBD0;
    }
    </style>
</head>

<body>

<div id="content" role="main" class="container-fluid">
    <section class="row colset-2-its">
        <ul class="nav nav-tabs" id="myTab" role="tablist">
            <li class="nav-item active">
                <a class="nav-link" id="summary-tab" data-toggle="tab" href="#summary" role="tab"
                   aria-controls="summary" aria-selected="true" aria-expanded="true">Summary</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" id="details-tab" data-toggle="tab" href="#details" role="tab"
                   aria-controls="details" aria-selected="false">Details</a>
            </li>
        </ul>

        <div class="tab-content" id="myTabContent">
            <div class="tab-pane fade active in" id="summary" role="tabpanel" aria-labelledby="summary-tab">
                <g:render template="summary" model="${[items: summary]}"/>
                <g:render template="pivot" model="${[data: clientSummary, label: 'Client']}"/>
                <g:render template="pivot" model="${[data: projectSummary, label: 'Project']}"/>
                <g:render template="pivot" model="${[data: componentSummary, label: 'Component']}"/>
            </div>

            <div class="tab-pane fade" id="details" role="tabpanel" aria-labelledby="details-tab">
                <g:render template="details" model="${[worklogs: worklogs]}"/>
            </div>
        </div>
    </section>
</div>
<script language="JavaScript" type="text/javascript">
    $(document).ready(function () {
        $('.list').DataTable({
            scrollY: "400px",
            scrollCollapse: true,
            paging: false,
            fixedHeader: true,
            dom: 'Bfrtip',
            buttons: [
                'copy', 'csv', 'excel', 'pdf', 'print'
            ]
        });
        $('.pivot').DataTable({
            scrollY: "400px",
            scrollX: true,
            scrollCollapse: true,
            paging: false,
            fixedHeader: true,
            fixedColumns: {
                leftColumns: 1
            },
            dom: 'Bfrtip',
            buttons: [
                'copy', 'csv', 'excel', 'pdf', 'print'
            ]
        });
    });
</script>
</body>
</html>