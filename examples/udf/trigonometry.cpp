/* This file is part of VoltDB.
 * Copyright (C) 2008-2016 VoltDB Inc.
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

#include "udf/UDF.h"
#define PI 3.14159

namespace voltdb {

class Sine : public ScalarFunction {
public:
    Sine() {
        addArgumentOfType(VALUE_TYPE_DOUBLE);
        setReturnType(VALUE_TYPE_DOUBLE);
    }
    NValue p_execute() {
        double degree = getDoubleArgument(0);
        return ValueFactory::getDoubleValue(std::sin(degree));
    }
};

class Cosine : public ScalarFunction {
public:
    Cosine() {
        addArgumentOfType(VALUE_TYPE_DOUBLE);
        setReturnType(VALUE_TYPE_DOUBLE);
    }
    NValue p_execute() {
        double degree = getDoubleArgument(0);
        return ValueFactory::getDoubleValue(cos(degree / 180 * PI));
    }
};

class Tangent : public ScalarFunction {
public:
    Tangent() {
        addArgumentOfType(VALUE_TYPE_DOUBLE);
        setReturnType(VALUE_TYPE_DOUBLE);
    }
    NValue p_execute() {
        double degree = getDoubleArgument(0);
        return ValueFactory::getDoubleValue(tan(degree / 180 * PI));
    }
};


REGISTER_VOLTDB_SCALAR_UDF(Sine);
REGISTER_VOLTDB_SCALAR_UDF(Cosine);
REGISTER_VOLTDB_SCALAR_UDF(Tangent);

} // namespace voltdb
