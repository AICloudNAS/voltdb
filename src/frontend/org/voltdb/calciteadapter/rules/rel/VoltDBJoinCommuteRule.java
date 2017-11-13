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

package org.voltdb.calciteadapter.rules.rel;

import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.rules.JoinCommuteRule;
import org.voltdb.calciteadapter.rel.AbstractVoltDBJoin;
import org.voltdb.calciteadapter.rel.VoltDBNLJoin;

/**
 * The sole reason for this class is to limit the commute to the NLJ only.
 * Also, the original JoinCommuteRule.INSTACE matches LogicalJoin but AbstractVoltDBJoin
 * inherits directly from Join and not the Logical one which is final.
 *
 */
public class VoltDBJoinCommuteRule extends JoinCommuteRule {

    /** Instance of the rule that only swaps inner joins. */
    public static final VoltDBJoinCommuteRule INSTANCE = new VoltDBJoinCommuteRule(false);

    private VoltDBJoinCommuteRule(boolean swapOuter) {
        super(VoltDBNLJoin.class, RelFactories.LOGICAL_BUILDER, swapOuter);
    }

    @Override
    public void onMatch(final RelOptRuleCall call) {
        super.onMatch(call);
    }
}