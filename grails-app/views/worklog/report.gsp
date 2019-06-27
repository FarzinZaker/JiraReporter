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
                    <a class="nav-link" id="details-tab" data-toggle="tab" href="#details" role="tab"
                       aria-controls="details" aria-selected="false">Details</a>
                </li>
            </ul>

            <div class="tab-content" id="myTabContent">
                <div class="tab-pane fade" id="summary" role="tabpanel" aria-labelledby="summary-tab">
                    <g:render template="summary" model="${[items: summary]}"/>
                </div>

                <div class="tab-pane fade active in" id="users" role="tabpanel" aria-labelledby="users-tab">
                    <g:render template="pivot" model="${[data: clientSummary, label: 'Client']}"/>
                    <g:render template="pivot" model="${[data: projectSummary, label: 'Project']}"/>
                    <g:render template="pivot" model="${[data: componentSummary, label: 'Component']}"/>
                    <g:render template="pivot" model="${[data: issueTypeSummary, label: 'Issue Type']}"/>
                </div>

                <div class="tab-pane fade" id="details" role="tabpanel" aria-labelledby="details-tab">
                    <g:render template="details" model="${[worklogs: worklogs]}"/>
                </div>
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
                'copy', 'csv', 'excel'
            ],
            rowReorder: true
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
                'copy', 'csv', 'excel'
            ],
            colReorder: true,
            rowReorder: true
        });
    });
</script>
</body>
</html>