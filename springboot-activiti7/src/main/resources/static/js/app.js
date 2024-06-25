$(document).ready(function() {
    // 用户提交订单
    $('#orderForm').submit(function(event) {
        event.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/orders/start',
            data: $(this).serialize(),
            success: function(response) {
                $('#result').html(response);
            },
            error: function(error) {
                $('#result').html('Error: ' + error.responseText);
            }
        });
    });

    // 用户查看订单
    $('#viewUserOrdersForm').submit(function(event) {
        event.preventDefault();
        var username = $('#username').val();
        $.ajax({
            type: 'GET',
            url: '/orders/user-orders',
            data: { username: username },
            success: function(response) {
                var resultHtml = '<ul>';
                response.forEach(function(order) {
                    resultHtml += '<li>' + JSON.stringify(order) + '</li>';
                });
                resultHtml += '</ul>';
                $('#userOrdersResult').html(resultHtml);
            },
            error: function(error) {
                $('#userOrdersResult').html('Error: ' + error.responseText);
            }
        });
    });

    // 餐馆查看订单
    $('#viewRestaurantOrdersForm').submit(function(event) {
        event.preventDefault();
        var restaurantName = $('#restaurantName').val();
        $.ajax({
            type: 'GET',
            url: '/orders/restaurant-orders',
            data: { restaurantName: restaurantName },
            success: function(response) {
                var resultHtml = '<ul>';
                response.forEach(function(order) {
                    resultHtml += '<li>' + JSON.stringify(order) + '</li>';
                });
                resultHtml += '</ul>';
                $('#restaurantOrdersResult').html(resultHtml);
            },
            error: function(error) {
                $('#restaurantOrdersResult').html('Error: ' + error.responseText);
            }
        });
    });

    // 餐馆分配骑手
    $('#assignRiderForm').submit(function(event) {
        event.preventDefault();
        $.ajax({
            type: 'POST',
            url: '/orders/assign-rider',
            data: $(this).serialize(),
            success: function(response) {
                $('#assignRiderResult').html(response);
            },
            error: function(error) {
                $('#assignRiderResult').html('Error: ' + error.responseText);
            }
        });
    });

    // 骑手查看订单
    $('#viewRiderOrdersForm').submit(function(event) {
        event.preventDefault();
        var rider = $('#rider').val();
        $.ajax({
            type: 'GET',
            url: '/orders/rider-orders',
            data: { rider: rider },
            success: function(response) {
                var resultHtml = '<ul>';
                response.forEach(function(order) {
                    resultHtml += '<li>' + JSON.stringify(order) + '</li>';
                });
                resultHtml += '</ul>';
                $('#riderOrdersResult').html(resultHtml);
            },
            error: function(error) {
                $('#riderOrdersResult').html('Error: ' + error.responseText);
            }
        });
    });
});
