# Backend Engineering Take-Home: Provider Catalogue Synchronization

## The Product

Our users connect their accounting systems so they can work with their accounts and accounting dimensions in our product. Dimensions, such as department or project, let them categorize financial activity using values such as Sales or Product Launch.

We want users to have a catalogue they can trust as their accounting system changes, without having to maintain the same information in two places. They also add descriptions in our product to help their team understand how to use particular accounts or dimension values.

Build the backend for this experience using the supplied exports from three fictional accounting providers.

## What Users Need

- Bring accounts, dimensions, and dimension values from their connected accounting system into the product, and refresh them over time.
- Browse the catalogue and understand what is currently available to use.
- Add or edit a local description and keep it when the catalogue is refreshed.
- Get a useful explanation when something in the catalogue looks wrong or has changed.

Updates arrive over time, and the connection to a provider is not always reliable. Users should be able to trust what the product shows them and understand when it cannot give them an up-to-date catalogue.

We've noticed that provider exports don't always match their documented schemas. Keep that in mind when designing your solution.

## Starting Point

The [`fixtures`](fixtures) directory contains successive JSON exports from the three providers. Each provider describes a numbered revision as a complete snapshot of its catalogue.

Inspect the supplied data and decide what the product needs to handle. The behavior, architecture, and implementation choices are yours. Make reasonable assumptions where needed and explain them.

## What to Submit

Submit a GitHub repository or zip containing:

- a working backend with a small API or CLI that demonstrates the user needs above; no frontend is required;
- a repeatable demonstration using the supplied fixtures, including situations you think are important to handle;
- automated tests for the behavior you consider most important; and
- a short README with setup instructions, the problems you identified, your decisions, and any limitations or unfinished work.

A small UI can be a helpful way to demonstrate how your solution behaves and explain your decisions. It's optional and doesn't need to be polished.

We care about which problems you identify, how you prioritize them for users, and how well your implementation supports your choices. Be prepared to walk us through your solution and explain what you would do next.

Use any backend language and tools you are comfortable with. Aim to spend no more than eight hours; make deliberate scope choices rather than polishing every edge. You may use AI coding tools, but you remain responsible for understanding and defending the result.
