angular.module(PKG.name + '.feature.adapters')
  .controller('AdapterRunDetailLogController', function($scope, MyDataSource, $state, $q) {
    var dataSrc = new MyDataSource($scope),
        basePath = '/adapters/' + $state.params.adapterId +
                   '/runs/' + $scope.runs.selected.runid,
        logPath = '';

   dataSrc.request({
     _cdapPath: '/namespaces/' + $state.params.namespace +
                '/adapters/' + $state.params.adapterId
   })
    .then(function(res) {
      var appId = res.program.application.applicationId;
      var programId = res.program.id;
      logPath = '/apps/' + appId +
                '/'+ res.program.type.toLowerCase() + 's' +'/' + programId +
                '/runs/' + $scope.runs.selected.runid +
                '/logs/prev?adapterid=' + $state.params.adapterId;
      return $q.when(logPath);
    })
      .then(function(lpath) {
        dataSrc.poll({
          _cdapNsPath: lpath
        }, function(res) {
          $scope.logs = res;
        });
      })

    $scope.logs = [];
  });
