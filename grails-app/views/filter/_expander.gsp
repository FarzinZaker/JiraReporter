<div class="expander">
    <div class="expander-button">FILTERS</div>
</div>

<script language="JavaScript">
    $(document).ready(function () {
        $('.expander-button').click(function () {
            toggleFilterPanel();
        });

        resizeGantt();
    });

    function toggleFilterPanel() {

        var mainBody = $('.main-body')[0];
        if (mainBody.getAttribute('class').indexOf('fullscreen') === -1)
            $(mainBody).addClass('fullscreen');
        else
            $(mainBody).removeClass('fullscreen');
        resizeGantt()
    }

    function resizeGantt() {
        $('#gantt_here').height($(window).height() - 190);

        var mainBody = $('.main-body')[0];
        if (mainBody.getAttribute('class').indexOf('fullscreen') === -1) {
            $('#gantt_here').width($(window).width() - 360);
            $('.tab-content').width($(window).width() - 375);
        }
        else {
            $('#gantt_here').width($(window).width() - 60);
            $('.tab-content').width($(window).width() - 75);
        }
    }

    $(window).resize(function () {
        resizeGantt();
    });
</script>