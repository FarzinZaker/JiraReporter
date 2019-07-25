<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Worklog Report</title>
</head>

<body>

<div id="content" role="main" class="container-fluid">
    <section class="row colset-2-its">
        <div class="col-lg-2">
            <g:render template="/filter/filter"/>
        </div>

        <div class="col-lg-10">
            <ul class="nav nav-tabs" id="myTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link" id="summary-tab" data-toggle="tab" href="#summary" role="tab"
                       aria-controls="summary" aria-selected="false">Summary</a>
                </li>
                <li class="nav-item active">
                    <a class="nav-link" id="users-tab" data-toggle="tab" href="#users" role="tab"
                       aria-controls="users" aria-selected="true" aria-expanded="true">Users</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="crossOver-tab" data-toggle="tab" href="#crossOver" role="tab"
                       aria-controls="crossOver" aria-selected="false">CrossOver</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="details-tab" data-toggle="tab" href="#details" role="tab"
                       aria-controls="details" aria-selected="false">Details</a>
                </li>
            </ul>

            <div class="tab-content" id="myTabContent">
                <div class="tab-pane fade" id="summary" role="tabpanel" aria-labelledby="summary-tab">
                    <g:render template="summary" model="${[items: userSummary, label: 'Engineer', color: '#673AB7']}"/>
                    <g:render template="summary" model="${[items: clientSummary, label: 'Client', color: '#009688']}"/>
                    <g:render template="summary"
                              model="${[items: projectSummary, label: 'Project', color: '#3F51B5']}"/>
                    <g:render template="summary"
                              model="${[items: componentSummary, label: 'Component', color: '#E91E63']}"/>
                    <g:render template="summary"
                              model="${[items: issueTypeSummary, label: 'Issue Type', color: '#FF9800']}"/>
                </div>

                <div class="tab-pane fade active in" id="users" role="tabpanel" aria-labelledby="users-tab">
                    <g:render template="pivot" model="${[data: clientDetails, label: 'Client']}"/>
                    <g:render template="pivot" model="${[data: projectDetails, label: 'Project']}"/>
                    <g:render template="pivot" model="${[data: componentDetails, label: 'Component']}"/>
                    <g:render template="pivot" model="${[data: issueTypeDetails, label: 'Issue Type']}"/>
                </div>

                <div class="tab-pane fade" id="crossOver" role="tabpanel" aria-labelledby="crossOver-tab">
                    <g:render template="crossOver/total" model="${[summary: integritySummary.total, label: 'Total']}"/>
                    <g:render template="crossOver/daily" model="${[summary: integritySummary.daily, label: 'Daily']}"/>
                </div>

                <div class="tab-pane fade" id="details" role="tabpanel" aria-labelledby="details-tab">
                    <g:render template="details" model="${[worklogs: worklogs]}"/>
                </div>
            </div>
        </div>
    </section>
</div>

</body>
</html>