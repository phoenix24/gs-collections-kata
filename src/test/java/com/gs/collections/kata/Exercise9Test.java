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
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class Exercise9Test extends CompanyDomainForKata
{
    /**
     * Extra credit. Aggregate the total order values by city.  Hint: Look at RichIterable.aggregateBy.
     */
    @Test
    public void totalOrderValuesByCity()
    {
        Function0<Double> zeroValueFactory = new Function0<Double>()
        {
            public Double value()
            {
                return Double.valueOf(0.0);
            }
        };

        Function2<Double, Customer, Double> aggregator = new Function2<Double, Customer, Double>()
        {
            public Double value(Double result, Customer customer)
            {
                return result + customer.getTotalOrderValue();
            }
        };
        MutableMap<String, Double> map = this.company.getCustomers().aggregateBy(new Function<Customer, String>() {
            @Override
            public String valueOf(Customer customer) {
                return customer.getCity();
            }
        },
        zeroValueFactory,
        new Function2<Double, Customer, Double>() {
            @Override
            public Double value(Double argument1, Customer argument2) {
                return argument1 + argument2.getTotalOrderValue();
            }
        });
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(446.25, map.get("London"), 0.0);
        Assert.assertEquals(857.0, map.get("Liphook"), 0.0);
    }

    /**
     * Extra credit. Aggregate the total order values by item.  Hint: Look at RichIterable.aggregateBy and remember
     * how to use flatCollect to get an iterable of all items.
     */
    @Test
    public void totalOrderValuesByItem()
    {
        Function0<Double> zeroValueFactory = new Function0<Double>()
        {
            public Double value()
            {
                return Double.valueOf(0.0);
            }
        };

        Function2<Double, LineItem, Double> aggregator = new Function2<Double, LineItem, Double>()
        {
            public Double value(Double result, LineItem lineItem)
            {
                return result + lineItem.getValue();
            }
        };
        MutableMap<String, Double> map = this.company.getCustomers().flatCollect(new Function<Customer, MutableList<Order>>() {
            @Override
            public MutableList<Order> valueOf(Customer object) {
                return object.getOrders();
            }
        }).flatCollect(new Function<Order, List<LineItem>>() {
            @Override
            public List<LineItem> valueOf(Order order) {
                return order.getLineItems();
            }
        }).aggregateBy(new Function<LineItem, String>() {
            @Override
            public String valueOf(LineItem item) {
                return item.getName();
            }
        },
        zeroValueFactory,
        new Function2<Double, LineItem, Double>() {
            @Override
            public Double value(Double argument1, LineItem argument2) {
                return argument1 + argument2.getValue();
            }
        });

        Verify.assertSize(12, map);
        Assert.assertEquals(100.0, map.get("shed"), 0.0);
        Assert.assertEquals(10.5, map.get("cup"), 0.0);
    }

    /**
     * Extra credit. Figure out which customers ordered saucers (in any of their orders).
     */
    @Test
    public void whoOrderedSaucers()
    {
        final Predicate<Order> ORDERED_SAUCER = new Predicate<Order>() {
            @Override
            public boolean accept(Order each) {
                return FastList.newList(each.getLineItems()).anySatisfy(new Predicate<LineItem>() {
                    @Override
                    public boolean accept(LineItem each) {
                        return each.getName().equalsIgnoreCase("saucer");
                    }
                });
            }
        };

        final Predicate<Customer> CUSTOMERS_ORDERED_SAUCER = new Predicate<Customer>() {
            @Override
            public boolean accept(Customer customer) {
                return customer.getOrders().anySatisfy(ORDERED_SAUCER);
            }
        };

        MutableList<Customer> customersWithSaucers = this.company.getCustomers().select(CUSTOMERS_ORDERED_SAUCER);
        Verify.assertSize("customers with saucers", 2, customersWithSaucers);
    }

    /**
     * Extra credit. Look into the {@link MutableList#toMap(Function, Function)} method.
     */
    @Test
    public void ordersByCustomerUsingAsMap()
    {
        MutableMap<String, MutableList<Order>> customerNameToOrders = this.company.getCustomers()
            .toMap(
                    new Function<Customer, String>() {
                        @Override
                        public String valueOf(Customer object) {
                            return object.getName();
                        }
                    },
                    new Function<Customer, MutableList<Order>>() {
                        @Override
                        public MutableList<Order> valueOf(Customer object) {
                            return object.getOrders();
                        }
                    }
            );

        Assert.assertNotNull("customer name to orders", customerNameToOrders);
        Verify.assertSize("customer names", 3, customerNameToOrders);
        MutableList<Order> ordersForBill = customerNameToOrders.get("Bill");
        Verify.assertSize("Bill orders", 3, ordersForBill);
    }

    /**
     * Extra credit. Create a multimap where the values are customers and the key is the price of
     * the most expensive item that the customer ordered.
     */
    @Test
    public void mostExpensiveItem()
    {
        MutableListMultimap<Double, Customer> multimap = null;

        Assert.assertEquals(3, multimap.size());
        Assert.assertEquals(2, multimap.keysView().size());
        Assert.assertEquals(
                FastList.newListWith(
                        this.company.getCustomerNamed("Fred"),
                        this.company.getCustomerNamed("Bill")),
                multimap.get(50.0));
    }
}
