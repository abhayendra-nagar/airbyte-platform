/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.commons.auth;

/**
 * Collection of constants that defines authorization roles.
 */
public final class AuthRoleConstants {

  public static final String ADMIN = "ADMIN";
  public static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";
  public static final String EDITOR = "EDITOR";
  public static final String OWNER = "OWNER";
  public static final String NONE = "NONE";
  public static final String READER = "READER";

  public static final String ORGANIZATION_ADMIN = "ORGANIZATION_ADMIN";
  public static final String ORGANIZATION_EDITOR = "ORGANIZATION_EDITOR";
  public static final String ORGANIZATION_READER = "ORGANIZATION_READER";

  private AuthRoleConstants() {}

}
