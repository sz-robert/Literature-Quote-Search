angular.module('demo', [])
.controller('specialized_ops_titles', function($scope, $http) {
    $http.get('http://ec2-34-210-26-240.us-west-2.compute.amazonaws.com:8080/specialized_ops_titles').
        then(function(response) {
            $scope.greeting = response.data;
        });
});

//http://ec2-34-210-26-240.us-west-2.compute.amazonaws.com:8080/specialized_ops_titles
