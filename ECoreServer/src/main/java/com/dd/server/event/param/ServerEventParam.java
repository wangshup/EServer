package com.dd.server.event.param;

public enum ServerEventParam implements IServerEventParam
{
  ZONE,
  ROOM,
  USER,
  LOGIN_NAME,
  LOGIN_PASSWORD,
  LOGIN_IN_DATA,
  LOGIN_OUT_DATA,
  JOINED_ROOMS,
  PLAYER_ID,
  PLAYER_IDS_BY_ROOM,
  SESSION,
  DISCONNECTION_REASON,
  VARIABLES,
  RECIPIENT,
  MESSAGE,
  OBJECT;
}