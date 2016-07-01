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

First of all it's needed to setup `RabbitMQ`. It's easy and well described at: [their site](https://www.rabbitmq.com/download.html). I used `Homebrew` at my Mac. 

```
brew update
brew install rabbitmq
/usr/local/sbin/rabbitmq-server
```

Then you may check if it works by link: [http://localhost:15672/](http://localhost:15672/). Simple interface allows to create a queue and publish messages there. 

Compile and run `Worker` and `Manager` classes. The following `RabbitMQ` client lib is used: `com.rabbitmq:amqp-client:3.6.2`. First of all you may run a few `Workers` and then one `Manager` that will immidiately makes things run. 

## Code structure

The code is divided to three files:

1. `Generator.java` - is responsible for Armstrong Number calculations and throwing them to the queue. The calculation algorithm is described [here](https://github.com/shamily/ArmstrongNumbers). It uses `BigInteger`, while it would also be possible to execute specific algorithm depending of the size of the numbers which needs to be calculated.
2. `Worker.java` - uses `Generator.java` class and works with the relevant queues. It has a `main` method.
3. `Manager.java` - works with the relevant queues and prints the results to the console. It has a `main` method.

It's very good described [here](https://www.rabbitmq.com/tutorials/tutorial-one-java.html) how to work with `RabbitMQ` client in Java. 
