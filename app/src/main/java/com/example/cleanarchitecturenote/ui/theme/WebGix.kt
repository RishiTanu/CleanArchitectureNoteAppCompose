package com.example.cicdintegration.ui

class WebGix {

    fun startViewOnMap(
        context: Context,
        legLatLngGlobal: MutableList<RouteList>,
        showPlanned: Boolean,
        showCollision: Boolean,
        showFarAway: Boolean,
        showSafeZone: Boolean,
        onSimulationTimeStart: (date: String) -> Unit,
        fragmentManager: FragmentManager,
    ) {
        Log.d(
            "TAG",
            "drawAllPolylinesColor=== $showCollision  $showPlanned $showFarAway  $showSafeZone"
        )
        mapView.clear()

        if (showPlanned) {
            // Draw user route
            val myRoute = legLatLngGlobal.find { it.isSimulationRoute }
            myRoute?.let {
                drawUserRouteOnMap(it)
            }

            // Coroutine to draw routes asynchronously
            scope.launch {
                legLatLngGlobal.forEach { route ->
                    if (!route.isSimulationRoute) {
                        drawOtherRoute(context, route, showCollision, showFarAway, showSafeZone, fragmentManager)
                    } else {
                        drawSimulationRoute(context, route, onSimulationTimeStart, fragmentManager)
                    }
                }
            }
        } else {
            // Draw only aircraft if no planned route is shown
            legLatLngGlobal.forEach { route ->
                val startCoordinate = LatLng(
                    route.legs.first().startLatitude,
                    route.legs.first().startLongitude
                )
                val endCoordinate = LatLng(
                    route.legs.last().endLatitude,
                    route.legs.last().endLongitude
                )
                movingAircraftOnPath(
                    context,
                    fragmentManager,
                    route,
                    startCoordinate,
                    endCoordinate,
                    route.legs.first().endAltitude,
                    route.isSimulationRoute,
                    route.legs.first().groundSpeed
                )
            }
        }
    }

    private fun drawUserRouteOnMap(route: RouteList) {
        val startC = LatLng(route.legs.first().startLatitude, route.legs.first().startLongitude)
        val endC = LatLng(route.legs.last().endLatitude, route.legs.last().endLongitude)

        // Calculate route distance and number of points based on distance
        myRouteDistance = startC.haversineDistance(endC)
        val numberOfPoints = (myRouteDistance / 60).toInt()

        // Create a polyline for the user's route and add it to the map
        userRouteArray.add(createPolylineOptions(numberOfPoints, myRouteDistance, startC, endC, Color.BLUE))
        userRouteArray.forEach { mapView.addPolyline(it) }
        Log.d("TAG", "USER_ROUTE=====${userRouteArray.size}")
    }

    private suspend fun drawOtherRoute(
        context: Context,
        route: RouteList,
        showCollision: Boolean,
        showFarAway: Boolean,
        showSafeZone: Boolean,
        fragmentManager: FragmentManager
    ) {
        // Loop through each leg in the route
        route.legs.forEach { leg ->
            val startCoordinate = LatLng(leg.startLatitude, leg.startLongitude)
            val endCoordinate = LatLng(leg.endLatitude, leg.endLongitude)

            // Draw waypoints for each leg
            drawWaypointsForLeg(context, startCoordinate, endCoordinate)

            // Calculate route distance and number of points
            val legDistance = startCoordinate.haversineDistance(endCoordinate)
            val numberOfPoints = (legDistance / 60).toInt()

            val distanceDifference = Math.abs(myRouteDistance - legDistance)

            // Determine which type of polyline to draw (collision, far away, or safe zone)
            when {
                distanceDifference <= 700.0 && showCollision -> {
                    drawCollisionPolylines(startCoordinate, endCoordinate, numberOfPoints, legDistance)
                }
                distanceDifference <= 1000.0 && showFarAway -> {
                    drawNearbyPolylines(startCoordinate, endCoordinate, numberOfPoints, legDistance)
                }
                showSafeZone -> {
                    drawSafeZonePolylines(startCoordinate, endCoordinate, numberOfPoints, legDistance)
                }
            }

            // Draw moving aircraft on this route
            movingAircraftOnPath(
                context,
                fragmentManager,
                route,
                startCoordinate,
                endCoordinate,
                leg.endAltitude,
                route.isSimulationRoute,
                leg.groundSpeed
            )
        }
    }

    private suspend fun drawSimulationRoute(
        context: Context,
        route: RouteList,
        onSimulationTimeStart: (date: String) -> Unit,
        fragmentManager: FragmentManager
    ) {
        route.legs.forEach { leg ->
            // Handle collision waypoints
            leg.collisionGeopoints.forEach { point ->
                val latLng = LatLng(point.latitute, point.longitute)
                MapViewHelper.addMarkerCollisionPoint(latLng, point.color, mapView)
            }
        }

        // Set simulation timer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onSimulationTimeStart(route.startDatetime)
        }

        // Draw moving aircraft for the simulation route
        val startCoordinate = LatLng(route.legs.first().startLatitude, route.legs.first().startLongitude)
        val endCoordinate = LatLng(route.legs.last().endLatitude, route.legs.last().endLongitude)

        movingAircraftOnPath(
            context,
            fragmentManager,
            route,
            startCoordinate,
            endCoordinate,
            route.legs.first().endAltitude,
            true,
            route.legs.first().groundSpeed
        )
    }

    private fun drawWaypointsForLeg(context: Context, startCoordinate: LatLng, endCoordinate: LatLng) {
        val routeGeopoints = listOf(startCoordinate, endCoordinate)

        // Draw marker for each waypoint
        routeGeopoints.forEach { wayPoint ->
            val markerOptions = MarkerOptions().position(wayPoint).icon(MapUtil.getGlobalWayPoint(context))
            drawnMarker.add(mapView.addMarker(markerOptions)!!)
        }
    }

}