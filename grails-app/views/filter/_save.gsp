<input type="hidden" name="filter" value="${params.filter}"/>
<input type="button" onclick="createForm()" value="SAVE FILTER" class="k-button" style="float: right"/>

<div id="filterWindow"></div>
<script language="JavaScript" type="text/javascript">
    var filterWindow;
    $(document).ready(function () {
        filterWindow = $("#filterWindow")
            .kendoWindow({
                title: "Save Filter",
                modal: true,
                visible: false,
                resizable: false,
                width: 400
            }).data("kendoWindow");
    });
</script>