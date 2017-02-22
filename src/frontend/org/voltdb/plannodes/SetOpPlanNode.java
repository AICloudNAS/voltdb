/* This file is part of VoltDB.
 * Copyright (C) 2008-2017 VoltDB Inc.
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

package org.voltdb.plannodes;

import java.util.ArrayList;
import java.util.List;

import org.json_voltpatches.JSONException;
import org.json_voltpatches.JSONObject;
import org.json_voltpatches.JSONStringer;
import org.voltdb.catalog.Database;
import org.voltdb.expressions.AbstractExpression;
import org.voltdb.planner.PlanningErrorException;
import org.voltdb.types.PlanNodeType;
import org.voltdb.types.SetOpType;
import org.voltdb.types.SortDirectionType;

public class SetOpPlanNode extends AbstractPlanNode {

    private static class Members {
        static final String SETOP_TYPE = "SETOP_TYPE";
    }

    // SetOp Type
    private SetOpType m_setOpType;

    public SetOpPlanNode() {
        super();
        m_setOpType = SetOpType.NONE;
    }

    public SetOpPlanNode(SetOpType setOpType) {
        super();
        m_setOpType = setOpType;
    }

    @Override
    public PlanNodeType getPlanNodeType() {
        return PlanNodeType.SETOP;
    }

    @Override
    public void resolveColumnIndexes() {
        // Should be at least two children in a setOp
        assert(m_children.size() > 1);
        for (AbstractPlanNode child : m_children) {
            child.resolveColumnIndexes();
        }
    }

    public SetOpType getSetOpType() {
        return m_setOpType;
    }

    @Override
    public void generateOutputSchema(Database db) {
        // Should be at least two selects in a join
        assert(m_children.size() > 1);
        // The output schema for the setOp is the output schema from the first expression
        m_children.get(0).generateOutputSchema(db);
        m_outputSchema = m_children.get(0).getOutputSchema();
        ArrayList<SchemaColumn> outputColumns = m_outputSchema.getColumns();

        // Then generate schemas for the remaining ones and make sure that they are identical
        for (AbstractPlanNode child : m_children) {
            child.generateOutputSchema(db);
            NodeSchema schema = child.getOutputSchema();
            ArrayList<SchemaColumn> columns = schema.getColumns();
            if (columns.size() != outputColumns.size()) {
                throw new RuntimeException("Column number mismatch detected in rows of UNION");
            }
            for (int j = 0; j < outputColumns.size(); ++j) {
                if (outputColumns.get(j).getType() != columns.get(j).getType()) {
                    throw new PlanningErrorException("Incompatible data types in UNION");
                }
            }
        }

        assert(! hasInlineVarcharOrVarbinary());

        m_outputSchema = m_children.get(0).getOutputSchema();
        m_hasSignificantOutputSchema = false; // It's just the first child's
        // Then check that they have the same types
   }

    private boolean hasInlineVarcharOrVarbinary() {
        for (AbstractPlanNode child : m_children) {
            ArrayList<SchemaColumn> columns = child.getOutputSchema().getColumns();

            for (SchemaColumn scol : columns) {
                if (AbstractExpression.hasInlineVarType(scol.getExpression())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void toJSONString(JSONStringer stringer) throws JSONException {
        super.toJSONString(stringer);
        stringer.keySymbolValuePair(Members.SETOP_TYPE, m_setOpType.name());
    }

    @Override
    protected String explainPlanForNode(String indent) {
        return "SET OP " + m_setOpType.name();
    }

    @Override
    public void loadFromJSONObject(JSONObject jobj, Database db) throws JSONException {
        helpLoadFromJSONObject(jobj, db);
        m_setOpType = SetOpType.valueOf(jobj.getString(Members.SETOP_TYPE));
    }

    @Override
    public boolean isOutputOrdered (List<AbstractExpression> sortExpressions, List<SortDirectionType> sortDirections) {
        return false;
    }

    @Override
    public boolean isOrderDeterministic() {
        return false;
    }
}
