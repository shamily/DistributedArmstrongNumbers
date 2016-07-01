# Distributed Armstrong Numbers

**The goal** of this prototype is a simpliest implementation of distributed Armstrong Numbers (or whatever else) calculation using few workers and a queue.

**Disclaimer:** 

1. It's a prototype. The idea is to make things work, not make them reusable.
2. It's a prototype. I didn't write tests for it. The idea is to do a little as possible to make it work.
3. It's a prototype. No failover or proper exception handing is in place.
4. There is no configuration - code assumes it works with localhost only. Because... *it's still a prototype*.
5. It's possible to design the code better. For example, `Generator` should know nothing about `RabbitMQ`. But - see #1.

## Solution description

I've implemented two modules: `Manager` and `Worker` which use a distributed queue software (`RabbitMQ`) to communicate. 

A 'Manager' sends the lengths of Armstrong Number it wants to be generated to a queue called `Nquque`. For example:numbers from 1 to 19. Then it stars listening for a queue called `ArmstrongNumbers` and prints out everything it reads from the queue.

There could be a few `Worker`s launched. Each listens for the `Nqueue` queue. Once a number is available - a `Worker` calculates  Armstrong numbers of the required length and once found put them to the `ArmstrongNumbers` queue.

## Setup

**TBD**
