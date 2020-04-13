<!doctype html>
<html>
<head>
    <meta name="layout" content="mainStock"/>
    <title>GitHub Report</title>
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
                    <li class="nav-item active">
                        <a class="nav-link" id="languages-tab" data-toggle="tab" href="#languages" role="tab"
                           aria-controls="langugaes" aria-selected="false">Languages</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" id="heatMap-tab" data-toggle="tab" href="#heatMap" role="tab"
                           aria-controls="heatMap" aria-selected="true" aria-expanded="true">HeatMap</a>
                    </li>
                </ul>

                <div class="tab-content" id="myTabContent">
                    <div class="tab-pane fade active in" id="languages" role="tabpanel" aria-labelledby="languages-tab">
                        <g:render template="languages"/>
                    </div>

                    <div class="tab-pane fade" id="heatMap" role="tabpanel" aria-labelledby="heatMap-tab">
                        <g:render template="heatMap"/>
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