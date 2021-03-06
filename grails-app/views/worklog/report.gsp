<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Worklogs</title>
</head>

<body>

<div id="content" role="main">
    <table class="main-body">
        <tr>
            <td class="filter-column">
                <g:render template="filter"/>
            </td>

            <td class="expander-cell">
                <g:render template="/filter/expander"/>
            </td>

            <td class="body-column">

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
                        <a class="nav-link" id="accomplishments-tab" data-toggle="tab" href="#accomplishments"
                           role="tab"
                           aria-controls="accomplishments" aria-selected="false">Tasks</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" id="details-tab" data-toggle="tab" href="#details" role="tab"
                           aria-controls="details" aria-selected="false">Details</a>
                    </li>
                </ul>

                <div class="tab-content" id="myTabContent">
                    <div class="tab-pane fade" id="summary" role="tabpanel" aria-labelledby="summary-tab">
                        <g:render template="summary"
                                  model="${[items: userSummary, label: 'Engineer', timeColor: '#673AB7', tasksColor: '#B39DDB']}"/>
                        <g:render template="summary"
                                  model="${[items: clientSummary, label: 'Client', timeColor: '#009688', tasksColor: '#80CBC4']}"/>
                        <g:render template="summary"
                                  model="${[items: projectSummary, label: 'Project', timeColor: '#3F51B5', tasksColor: '#9FA8DA']}"/>
                        <g:render template="summary"
                                  model="${[items: componentSummary, label: 'Component', timeColor: '#E91E63', tasksColor: '#F48FB1']}"/>
                        <g:render template="summary"
                                  model="${[items: issueTypeSummary, label: 'Issue Type', timeColor: '#FF9800', tasksColor: '#FFCC80']}"/>
                    </div>

                    <div class="tab-pane fade active in" id="users" role="tabpanel" aria-labelledby="users-tab">
                        <g:render template="pivot" model="${[data: clientDetails, label: 'Client']}"/>
                        <g:render template="pivot" model="${[data: projectDetails, label: 'Project']}"/>
                        <g:render template="pivot" model="${[data: componentDetails, label: 'Component']}"/>
                        <g:render template="pivot" model="${[data: issueTypeDetails, label: 'Issue Type']}"/>
                    </div>

                    <div class="tab-pane fade" id="crossOver" role="tabpanel" aria-labelledby="crossOver-tab">
                        <g:render template="crossOver/total"
                                  model="${[summary: integritySummary.total, label: 'Total']}"/>
                        <g:render template="crossOver/daily"
                                  model="${[summary: integritySummary.daily, label: 'Daily']}"/>
                    </div>

                    <div class="tab-pane fade" id="accomplishments" role="tabpanel"
                         aria-labelledby="accomplishments-tab">
                        <g:render template="accomplishments" model="${[accomplishments: accomplishments]}"/>
                    </div>

                    <div class="tab-pane fade" id="details" role="tabpanel" aria-labelledby="details-tab">
                        <g:render template="details" model="${[worklogs: worklogs]}"/>
                    </div>
                </div>
            </td>
        </tr>
    </table>
</div>
<script>
    $(document).ready(function () {
        var url = document.location.toString();
        if (url.match('#')) {
            $('.nav-tabs #' + url.split('#')[1] + '-tab').click();
            setTimeout(function () {
                window.scrollTo(0, 0);
            }, 1000);
        }

        // Change hash for page-reload
        $('.nav-tabs a').on('shown.bs.tab', function (e) {
            window.location.hash = e.target.hash;
        })
    });
</script>

</body>
</html>