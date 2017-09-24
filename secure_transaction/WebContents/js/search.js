function next() {
    var form = document.getElementById("searchForm");
    var skipObj = document.getElementsByName("numResultsToSkip")[0];
    var retObj = document.getElementsByName("numResultsToReturn")[0];

    skipObj.value = parseInt(skipObj.value) + parseInt(retObj.value);
    form.submit();
}

function prev() {
    var form = document.getElementById("searchForm");
    var skipObj = document.getElementsByName("numResultsToSkip")[0];
    var retObj = document.getElementsByName("numResultsToReturn")[0];

    skipObj.value = parseInt(skipObj.value) - parseInt(retObj.value);
    form.submit();
}

function firstPage() {
    var form = document.getElementById("searchForm");
    var skipObj = document.getElementsByName("numResultsToSkip")[0];
    var retObj = document.getElementsByName("numResultsToReturn")[0];

    skipObj.value = "0";
    form.submit();
}
