function next() {
    var form = document.getElementById("searchForm");
    var skipObj = document.getElementsByName("numResultsToSkip")[0];

    skipObj.value = parseInt(skipObj.value) + 30;
    form.submit();
}

function prev() {
    var form = document.getElementById("searchForm");
    var skipObj = document.getElementsByName("numResultsToSkip")[0];

    if (skipObj.value !== "0") {
        if (parseInt(skipObj.value) < 30)
            skipObj.value = "0";
        else
            skipObj.value = parseInt(skipObj.value) - 30;
    }
    else
        return;
    form.submit();
}

function firstPage() {
    var form = document.getElementById("searchForm");
    var skipObj = document.getElementsByName("numResultsToSkip")[0];

    skipObj.value = "0";
    form.submit();
}
