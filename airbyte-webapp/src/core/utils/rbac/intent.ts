import { useRbac } from "./rbac";
import { RbacQuery, RbacQueryWithoutResourceId, RbacResource } from "./rbacPermissionsQuery";

const intentToRbacQuery = {
  ListOrganizationMembers: { resourceType: "ORGANIZATION", role: "READER" },
  UpdateOrganizationPermissions: { resourceType: "ORGANIZATION", role: "ADMIN" },

  UpdateWorkspacePermissions: { resourceType: "WORKSPACE", role: "ADMIN" },
  ListWorkspaceMembers: { resourceType: "WORKSPACE", role: "READER" },
} as const;

export type Intent = keyof typeof intentToRbacQuery;

interface OrganizationIntentMeta {
  organizationId?: string;
}
interface WorkspaceIntentMeta {
  workspaceId?: string;
}

// Utility types to enforce shape of any meta object; i.e. organzaition-focused intents shouldn't receive workspaceIds
// provides proper autocomplete hinting for `meta` and to future proof changes to Intent:Query mapping,
// e.g. changing Intent X from resource:"WORKSPACE" to resource:"ORGANIZATION" should catch instances of invalid meta
type MapIntentToResource<I extends Intent> = (typeof intentToRbacQuery)[I]["resourceType"];
type IntentMetaForResource<R extends RbacResource> = R extends "INSTANCE"
  ? Record<string, never>
  : R extends "ORGANIZATION"
  ? OrganizationIntentMeta
  : R extends "WORKSPACE"
  ? WorkspaceIntentMeta
  : never;

/*
Given the React context + overrides provided in optional `details`,
determine if the user has the required permissions to perform the given `intent`.
*/
export const useIntent = <I extends Intent>(intent: I, meta?: IntentMetaForResource<MapIntentToResource<I>>) => {
  let query: RbacQuery | RbacQueryWithoutResourceId = intentToRbacQuery[intent];
  if (isOrganizationIntentMeta(query.resourceType, meta) && meta?.organizationId) {
    query = { ...query, resourceId: meta?.organizationId };
  } else if (isWorkspaceIntentMeta(query.resourceType, meta) && meta?.workspaceId) {
    query = { ...query, resourceId: meta?.workspaceId };
  }

  return useRbac(query);
};

function isOrganizationIntentMeta(
  resource: RbacResource,
  _meta: OrganizationIntentMeta | WorkspaceIntentMeta | undefined
): _meta is OrganizationIntentMeta | undefined {
  return resource === "ORGANIZATION";
}

function isWorkspaceIntentMeta(
  resource: RbacResource,
  _meta: OrganizationIntentMeta | WorkspaceIntentMeta | undefined
): _meta is WorkspaceIntentMeta | undefined {
  return resource === "WORKSPACE";
}
