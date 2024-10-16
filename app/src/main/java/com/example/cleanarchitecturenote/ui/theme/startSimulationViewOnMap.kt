package com.example.cleanarchitecturenote.ui.theme

fun startSimulationViewOnMap(
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

        //show all routes
        if (showPlanned) {
            //all routes will be drawn
            val myRoute = legLatLngGlobal.find { it.isSimulationRoute }
            // myRoute.legs
            val startC = LatLng(
                myRoute?.legs?.first()?.startLatitude ?: 0.0,
                myRoute?.legs?.first()?.startLongitude ?: 0.0
            )
            val endC = LatLng(
                myRoute?.legs?.last()?.endLatitude ?: 0.0,
                myRoute?.legs?.last()?.endLongitude ?: 0.0
            )

            myRouteDistance = startC.haversineDistance(endC)
            var numberOfPoints = (myRouteDistance / 60).toInt()

            // Assign a unique color to the user's route
            userRouteArray.add(
                createPolylineOptions(
                    numberOfPoints,
                    myRouteDistance,
                    startC,
                    endC,
                    Color.BLUE
                )
            )

            userRouteArray.forEach {
                mapView.addPolyline(it)
            }
            Log.d("TAG", "USER_ROUTE=====${userRouteArray.size}")


            scope.launch {
                legLatLngGlobal.forEach { mapPolylineModal ->
                    //showing user route and markers
                    for (leg in mapPolylineModal.legs) {
                        legObj = leg
                        val routeGeopoints = mutableListOf<LatLng>()
                        routeGeopoints.add(LatLng(leg.startLatitude, leg.startLongitude))
                        routeGeopoints.add(LatLng(leg.endLatitude, leg.endLongitude))

                        routeGeopoints.forEach { wayPoint ->
                            val markerOptions = MarkerOptions().position(wayPoint)
                                .icon(MapUtil.getGlobalWayPoint(context))
                            drawnMarker.add(mapView.addMarker(markerOptions)!!)
                        }
                    }

                    if (!mapPolylineModal.isSimulationRoute) {
                        startCoordinate = LatLng(
                            mapPolylineModal.legs.first().startLatitude,
                            mapPolylineModal.legs.first().startLongitude
                        )
                        endCoordinate = LatLng(
                            mapPolylineModal.legs.last().endLatitude,
                            mapPolylineModal.legs.last().endLongitude
                        )

                        val otherRouteDistance = startCoordinate.haversineDistance(endCoordinate)
                        numberOfPoints = (otherRouteDistance / 60).toInt()

                        val distanceDifference = Math.abs(myRouteDistance - otherRouteDistance)
                        Log.d(
                            "TAG",
                            "drawAllPolylines: 30000  $myRouteDistance  $otherRouteDistance ${distanceDifference}"
                        )

                        when {
                            distanceDifference <= 700.0 -> {
                                if (showCollision) {
                                    collisionPolylines.add(
                                        createPolylineOptions(
                                            numberOfPoints,
                                            otherRouteDistance,
                                            startCoordinate,
                                            endCoordinate,
                                            Color.RED
                                        )
                                    )
                                    collisionPolylines.forEach {
                                        mapView.addPolyline(it)
                                    }
                                    Log.d(
                                        "TAG",
                                        "COLLISION_DIF: 30000 ${collisionPolylines.size}  $showCollision"
                                    )
                                } else {
                                    collisionPolylines.clear()
                                }

                            }

                            distanceDifference <= 1000.0 -> {
                                if (showFarAway) {
                                    nearbyPolylines.add(
                                        createPolylineOptions(
                                            numberOfPoints,
                                            otherRouteDistance,
                                            startCoordinate,
                                            endCoordinate,
                                            Color.YELLOW
                                        )
                                    )
                                    nearbyPolylines.forEach {
                                        mapView.addPolyline(it)
                                    }
                                    Log.d(
                                        "TAG",
                                        "COLLISION_DIF: 30000 ${nearbyPolylines.size}  $showFarAway"
                                    )
                                } else {
                                    nearbyPolylines.clear()
                                }
                            }

                            else -> {
                                if (showSafeZone) {
                                    safeDistancePolylines.add(
                                        createPolylineOptions(
                                            numberOfPoints,
                                            otherRouteDistance,
                                            startCoordinate,
                                            endCoordinate,
                                            Color.GREEN
                                        )
                                    )
                                    safeDistancePolylines.forEach {
                                        mapView.addPolyline(it)
                                    }
                                    Log.d(
                                        "TAG",
                                        "COLLISION_DIF: 30000 ${nearbyPolylines.size}  $showFarAway"
                                    )
                                } else {
                                    safeDistancePolylines.clear()
                                }
                            }
                        }

                        //will run for each route need one Aircraft object
                        movingAircraftOnPath(
                            context,
                            fragmentManager,
                            mapPolylineModal,
                            startCoordinate,
                            endCoordinate,
                            mapPolylineModal.legs.first().endAltitude,
                            mapPolylineModal.isSimulationRoute,
                            mapPolylineModal.legs.first().groundSpeed
                        )

                    } else {
                        //user route show collision and their flight timer and Aircraft marker
                        for (pilotRoute in mapPolylineModal.legs) {
                            if (pilotRoute.collisionGeopoints.size > 0) {
                                for (i in 1 until pilotRoute.collisionGeopoints.size) {
                                    val points = pilotRoute.collisionGeopoints.get(i)
                                    val lat = LatLng(points.latitute, points.longitute)
                                    MapViewHelper.addMarkerCollisionPoint(
                                        lat,
                                        points.color,
                                        mapView
                                    )
                                }
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            onSimulationTimeStart(mapPolylineModal.startDatetime)
                            //  simulationTimerStart(mapPolylineModal.startDatetime)
                        }

                        startCoordinate = LatLng(
                            mapPolylineModal.legs.first().startLatitude,
                            mapPolylineModal.legs.first().startLongitude
                        )
                        endCoordinate = LatLng(
                            mapPolylineModal.legs.last().endLatitude,
                            mapPolylineModal.legs.last().endLongitude
                        )


                        movingAircraftOnPath(
                            context,
                            fragmentManager,
                            mapPolylineModal,
                            startCoordinate,
                            endCoordinate,
                            mapPolylineModal.legs.first().endAltitude,
                            true,
                            mapPolylineModal.legs.first().groundSpeed,
                        )
                    }
                }
            }
        } else {
            //otherwise only aircraft will be drawn
            legLatLngGlobal.forEach { mapPolylineModal ->
                startCoordinate = LatLng(
                    mapPolylineModal.legs.first().startLatitude,
                    mapPolylineModal.legs.first().startLongitude
                )
                endCoordinate = LatLng(
                    mapPolylineModal.legs.last().endLatitude,
                    mapPolylineModal.legs.last().endLongitude
                )
                movingAircraftOnPath(
                    context,
                    fragmentManager,
                    mapPolylineModal,
                    startCoordinate,
                    endCoordinate,
                    mapPolylineModal.legs.first().endAltitude,
                    mapPolylineModal.isSimulationRoute,
                    mapPolylineModal.legs.first().groundSpeed,
                )
            }
        }
    }