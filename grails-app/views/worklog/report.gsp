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
        box-shadow: 2px 2px 5px gray;
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

    .user img {
        width: 48px;
        height: 48px;
        border-radius: 24px;
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
    </style>
</head>

<body>

<div id="content" role="main">
    <section class="row colset-2-its">
        <ul class="nav nav-tabs" id="myTab" role="tablist">
            <li class="nav-item active">
                <a class="nav-link active" id="summary-tab" data-toggle="tab" href="#summary" role="tab" aria-controls="summary" aria-selected="true" aria-expanded="true">Summary</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" id="details-tab" data-toggle="tab" href="#details" role="tab" aria-controls="details" aria-selected="false">Details</a>
            </li>
        </ul>

        <div class="tab-content" id="myTabContent">
            <div class="tab-pane fade active in" id="summary" role="tabpanel" aria-labelledby="summary-tab">
                <g:render template="summary" model="${[items: summary]}"/>
            </div>

            <div class="tab-pane fade" id="details" role="tabpanel" aria-labelledby="details-tab">
                <g:render template="details" model="${[worklogs: details]}"/>
            </div>
        </div>
    </section>
</div>
</body>
</html>