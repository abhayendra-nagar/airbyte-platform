/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.config.helpers;

import io.airbyte.commons.version.Version;
import io.airbyte.config.ActorDefinitionBreakingChange;
import jakarta.inject.Singleton;
import java.util.List;

/**
 * Helper class containing logic related to breaking changes.
 */
@Singleton
public class BreakingChangesHelper {

  /**
   * Given a current version and a version to upgrade to, and a list of breaking changes, determine
   * whether actors' default versions should be updated during upgrade. This logic is used to avoid
   * applying a breaking change to a user's actor.
   *
   * @param currentDockerImageTag version to upgrade from
   * @param dockerImageTagForUpgrade version to upgrade to
   * @param breakingChangesForDef a list of breaking changes to check
   * @return whether actors' default versions should be updated during upgrade
   */
  public static boolean shouldUpdateActorsDefaultVersionsDuringUpgrade(final String currentDockerImageTag,
                                                                       final String dockerImageTagForUpgrade,
                                                                       final List<ActorDefinitionBreakingChange> breakingChangesForDef) {
    if (breakingChangesForDef.isEmpty()) {
      // If there aren't breaking changes, early exit in order to avoid trying to parse versions.
      // This is helpful for custom connectors or local dev images for connectors that don't have
      // breaking changes.
      return true;
    }

    final Version currentVersion = new Version(currentDockerImageTag);
    final Version versionToUpgradeTo = new Version(dockerImageTagForUpgrade);

    if (versionToUpgradeTo.lessThanOrEqualTo(currentVersion)) {
      // When downgrading, we don't take into account breaking changes/hold actors back.
      return true;
    }

    final boolean upgradingOverABreakingChange = breakingChangesForDef.stream().anyMatch(
        breakingChange -> currentVersion.lessThan(breakingChange.getVersion()) && versionToUpgradeTo.greaterThanOrEqualTo(
            breakingChange.getVersion()));
    return !upgradingOverABreakingChange;
  }

}
