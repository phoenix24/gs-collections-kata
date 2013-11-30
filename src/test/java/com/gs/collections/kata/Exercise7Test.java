/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.kata;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class Exercise7Test extends CompanyDomainForKata
{

    final Function<Customer, Double> CUSTOMER_TOTAL_ORDERS = new Function<Customer, Double>() {
        @Override
        public Double valueOf(Customer object) {
            return new Double(object.getTotalOrderValue());
        }
    };

    /**
     * Get a list of the customers' total order values, sorted. Check out the implementation of {@link
     * Customer#getTotalOrderValue()} and {@link Order#getValue()} .
     */
    @Test
    public void sortedTotalOrderValue()
    {
        MutableList<Double> sortedTotalValues = this.company.getCustomers().collect(CUSTOMER_TOTAL_ORDERS).sortThis();

        // Don't forget the handy utility methods getFirst() and getLast()...
        Assert.assertEquals("Highest total order value", Double.valueOf(857.0), sortedTotalValues.getLast());
        Assert.assertEquals("Lowest total order value", Double.valueOf(71.0), sortedTotalValues.getFirst());
    }

    /**
     * Find the max total order value across all customers.
     */
    @Test
    public void maximumTotalOrderValue()
    {
        Double maximumTotalOrderValue = this.company.getCustomers().collect(CUSTOMER_TOTAL_ORDERS).max();
        Assert.assertEquals("max value", Double.valueOf(857.0), maximumTotalOrderValue);
    }

    /**
     * Find the customer with the highest total order value.
     */
    @Test
    public void customerWithMaxTotalOrderValue()
    {
        Customer customerWithMaxTotalOrderValue = this.company.getCustomers().maxBy(new Function<Customer, Comparable>() {
            @Override
            public Comparable valueOf(Customer object) {
                return object.getTotalOrderValue();
            }
        });
        Assert.assertEquals(this.company.getCustomerNamed("Mary"), customerWithMaxTotalOrderValue);
    }

    /**
     * Create some code to get the company's supplier names as a tilde delimited string.
     */
    @Test
    public void supplierNamesAsTildeDelimitedString()
    {
        String tildeSeparatedNames = ArrayIterate.makeString(this.company.getSuppliers(), "~");
        Assert.assertEquals(
                "tilde separated names",
                "Shedtastic~Splendid Crocks~Annoying Pets~Gnomes 'R' Us~Furniture Hamlet~SFD~Doxins",
                tildeSeparatedNames);
    }

    /**
     * Deliver all orders going to customers from London.
     *
     * @see Order#deliver()
     */
    @Test
    public void deliverOrdersToLondon()
    {
        Function<Customer, MutableList<Order>> ORDERS = new Function<Customer, MutableList<Order>>() {
            @Override
            public MutableList<Order> valueOf(Customer object) {
                return object.getOrders();
            }
        };

        MutableList<Customer> customers = this.company.getCustomers().select(Customer.FROM_LONDON);
        customers.flatCollect(ORDERS).forEach(new Procedure<Order>() {
            @Override
            public void value(Order each) {
                each.deliver();
            }
        });

        Verify.assertAllSatisfy(this.company.getCustomerNamed("Fred").getOrders(), Order.IS_DELIVERED);
        Verify.assertAllSatisfy(this.company.getCustomerNamed("Mary").getOrders(), Predicates.not(Order.IS_DELIVERED));
        Verify.assertAllSatisfy(this.company.getCustomerNamed("Bill").getOrders(), Order.IS_DELIVERED);
    }
}
