function formatDuration(time) {
    var secs = time;
    var mins = 0;
    var hours = 0;
    var days = 0;
    var weeks = 0;

    mins = ((secs - (secs % 60)) / 60);
    secs = (secs % 60);

    hours = ((mins - (mins % 60)) / 60);
    mins = (mins % 60);

    days = ((hours - (hours % 8)) / 8);
    hours = (hours % 8);

    weeks = ((days - (days % 5)) / 5);
    days = (days % 5);

    var timeSpent = '';
    if (weeks > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += weeks + 'w';
    }
    if (days > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += days + 'd';
    }
    if (hours > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += hours + 'h';
    }
    if (mins > 0) {
        if (timeSpent !== '')
            timeSpent += ' ';
        timeSpent += mins + 'm';
    }

    if (timeSpent === '')
        timeSpent = '0';

    return timeSpent
}

function getDurationSeconds(duration) {
    if (!duration)
        return 0;

    var seconds = 0;
    var parts = duration.split(' ');
    for (var i = 0; i < parts.length; i++) {
        var dur = parts[i];
        if (dur.endsWith('w'))
            seconds += parseInt(dur.replace('w', '')) * 60 * 60 * 8 * 5;
        else if (dur.endsWith('d'))
            seconds += parseInt(dur.replace('d', '')) * 60 * 60 * 8;
        else if (dur.endsWith('h'))
            seconds += parseInt(dur.replace('h', '')) * 60 * 60;
        else
            seconds += parseInt(dur.replace('m', '')) * 60 * 60;
    }
    return seconds;
}

function reformatDuration(duration) {
    // console.log(getDurationSeconds(duration));
    // console.log(formatDuration(getDurationSeconds(duration)));
    return formatDuration(getDurationSeconds(duration));
}

function validateDuration(duration) {
    try {
        var seconds = getDurationSeconds(duration);
        // console.log(seconds);
        if ((seconds > 0) && formatDuration(seconds))
            return true;
        else
            return false;
    } catch (error) {
        // console.log(error);
        return false;
    }
}