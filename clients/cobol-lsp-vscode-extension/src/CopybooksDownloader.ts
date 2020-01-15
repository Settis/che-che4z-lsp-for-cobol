/*
 * Copyright (c) 2020 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Broadcom, Inc. - initial API and implementation
 */

import { IProfile } from "@zowe/imperative";
import * as fs from "fs";
import * as path from "path";
import * as vscode from "vscode";
import { ProfilesMap, ZoweApi } from "./ZoweApi";

export const DEPENDENCIES_FOLDER: string = ".cobdeps";
export const COPYBOOKS_FOLDER: string = ".copybooks";

const SETTINGS_ROOT = "cobol-language-support";

export class CopybooksDownloader {
    public constructor(private zoweApi: ZoweApi) { }

    // tslint:disable-next-line: cognitive-complexity
    public async downloadCopyBooks(copybooks: string[]) {
        if (vscode.workspace.workspaceFolders.length === 0) {
            vscode.window.showErrorMessage("No workspace folder opened.");
            return;
        }
        const cb: Set<string> = new Set(copybooks);
        const profile = await this.askProfile();
        if (!profile) {
            return;
        }
        vscode.window.withProgress(
            {
                location: vscode.ProgressLocation.Notification,
                title: "Fetching copybooks",
            },
            async (progress: vscode.Progress<{ message?: string; increment?: number }>) => {
                for (const ds of await this.listPathDatasets()) {
                    progress.report({ message: "Looking in " + ds + ". " + cb.size + " copybook(s) left." });
                    try {
                        const members: string[] = await this.zoweApi.listMembers(ds, profile);
                        for (const member of members) {
                            if (cb.has(member)) {
                                await this.downloadCopybook(ds, member, profile);
                                cb.delete(member);
                                if (cb.size === 0) {
                                    return;
                                }
                            }
                        }
                    } catch (e) {
                        vscode.window.showErrorMessage(e.toString());
                    }
                }
            });
    }

    private async downloadCopybook(dataset: string, copybook: string, profile: IProfile) {
        const rootPath = vscode.workspace.workspaceFolders[0].uri.fsPath;
        const copybookDirPath = path.join(rootPath, COPYBOOKS_FOLDER, dataset);
        const copybookPath = path.join(copybookDirPath, copybook + ".cpy");

        fs.mkdirSync(copybookDirPath, { recursive: true });
        if (!fs.existsSync(copybookPath)) {
            const content = await this.zoweApi.fetchMember(dataset, copybook, profile);
            fs.writeFileSync(copybookPath, content);
        }
    }

    private async listPathDatasets(): Promise<string[]> {
        if (!vscode.workspace.getConfiguration(SETTINGS_ROOT).has("paths")) {
            // TODO may be replace with throw
            await vscode.window.showErrorMessage("Please, specify DATASET paths for copybooks in settings.");
            return [];
        }
        return vscode.workspace.getConfiguration(SETTINGS_ROOT).get("paths");
    }

    private async askProfile(): Promise<IProfile> {
        const profiles: ProfilesMap = await this.zoweApi.listZOSMFProfiles();
        if (Object.keys(profiles).length === 0) {
            // TODO mey be replace with throw
            await vscode.window.showErrorMessage("Zowe profile is missing.");
            return undefined;
        }
        if (Object.keys(profiles).length === 1) {
            return profiles[Object.keys(profiles)[0]];
        }
        const defaultName = this.zoweApi.getDefaultProfileName();
        const items: vscode.QuickPickItem[] = Object.keys(profiles).map(name => {
            const profile: IProfile = profiles[name];
            return {
                description: profile.username + "@" + profile.host + ":" + profile.port,
                label: name,
                picked: name === defaultName,
            };
        });

        const selectedProfile = await vscode.window.showQuickPick(items,
            { placeHolder: items[0].label, canPickMany: false });
        if (selectedProfile) {
            return profiles[selectedProfile.label];
        }
        return undefined;
    }
}
