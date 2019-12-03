<div class="expander">
    <div class="expander-button">FILTERS</div>
</div>

<script language="JavaScript">
    $(document).ready(function () {
        $('.expander-button').click(function () {
            var mainBody = $('.main-body')[0];
            if (mainBody.getAttribute('class').indexOf('fullscreen') === -1)
                $(mainBody).addClass('fullscreen');
            else
                $(mainBody).removeClass('fullscreen');
        });
    });
</script>