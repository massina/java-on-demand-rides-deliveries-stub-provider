/* Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.provider.json;

import com.example.provider.json.Waypoint.WaypointType;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import google.maps.fleetengine.v1.Trip;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/** Serializer for trip object to provide relevant information to its clients. */
final class TripSerializer implements JsonSerializer<Trip> {

  @Override
  public JsonElement serialize(Trip src, Type typeOfSrc, JsonSerializationContext context) {
    Waypoint pickupWaypoint =
        Waypoint.newBuilder()
            .setLocation(
                SerializedLocation.newBuilder().setPoint(src.getPickupPoint().getPoint()).build())
            .setWaypointType(WaypointType.PICKUP_WAYPOINT_TYPE)
            .build();

    Waypoint dropoffWaypoint =
        Waypoint.newBuilder()
            .setLocation(
                SerializedLocation.newBuilder().setPoint(src.getDropoffPoint().getPoint()).build())
            .setWaypointType(WaypointType.DROP_OFF_WAYPOINT_TYPE)
            .build();

    List<Waypoint> intermediateWaypoints =
        src.getIntermediateDestinationsList().stream()
            .map(
                destination ->
                    Waypoint.newBuilder()
                        .setLocation(
                            SerializedLocation.newBuilder()
                                .setPoint(destination.getPoint())
                                .build())
                        .setWaypointType(WaypointType.INTERMEDIATE_DESTINATION_WAYPOINT_TYPE)
                        .build())
            .collect(Collectors.toList());
    ;

    ImmutableList<Waypoint> waypoints =
        ImmutableList.<Waypoint>builder()
            .add(pickupWaypoint)
            .addAll(intermediateWaypoints)
            .add(dropoffWaypoint)
            .build();

    SerializedTrip trip =
        SerializedTrip.newBuilder()
            .setName(src.getName())
            .setTripStatus(src.getTripStatus().name())
            .setWaypoints(waypoints)
            .setVehicleId(src.getVehicleId())
            .build();

    return new Gson().toJsonTree(trip);
  }
}
