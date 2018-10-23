/* This file is part of VoltDB.
 * Copyright (C) 2008-2018 VoltDB Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VoltDB.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.voltdb.sysprocs;

import java.util.List;
import java.util.Map;

import org.voltdb.DependencyPair;
import org.voltdb.ParameterSet;
import org.voltdb.SystemProcedureExecutionContext;
import org.voltdb.VoltSystemProcedure;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;
import org.voltdb.VoltTable.ColumnInfo;
import org.voltdb.export.ExportManager;

public class ExportControl extends VoltSystemProcedure {

    // support operations
    public static enum EXPORT_CONTROL_OP{
        SKIP,
        PAUSE,
        RESUME
    }
    @Override
    public long[] getPlanFragmentIds() {
        return null;
    }

    @Override
    public DependencyPair executePlanFragment(Map<Integer, List<VoltTable>> dependencies, long fragmentId,
            ParameterSet params, SystemProcedureExecutionContext context) {
        return null;
    }

    public VoltTable[] run(SystemProcedureExecutionContext ctx, String stream, String target, String op) {
        VoltTable t = new VoltTable(
                new ColumnInfo("STATUS", VoltType.BIGINT),
                new ColumnInfo("MESSAGE", VoltType.STRING));
        try {
            EXPORT_CONTROL_OP.valueOf(op.toUpperCase());
        } catch (IllegalArgumentException e){
            t.addRow(VoltSystemProcedure.STATUS_FAILURE, "Invalide operation");
            return (new VoltTable[] {t});
        }

        if (ctx.isLowestSiteId()) {
            String error= ExportManager.instance().updateExportFlowControl(stream, target, op);
            if (error != null) {
                t.addRow(VoltSystemProcedure.STATUS_FAILURE, error);
                return (new VoltTable[] {t});
            }
        }
        t.addRow(VoltSystemProcedure.STATUS_OK, "");
        return (new VoltTable[] {t});
    }
}
