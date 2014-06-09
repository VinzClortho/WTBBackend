WTBBackend
==========

Vehicle tracking backend server using GTFS tables and incoming GPS data packets to determine and estimate routing
and stop arrival times.  The server binds to two TCP/IP ports: one for incoming GPS packets and the other will be
for the query portal.  The backend is still in early development stages, but already accepts most GTFS tables and
allows virtual drones to be run for heuristics testing.
