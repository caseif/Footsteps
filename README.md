# Footsteps

## What is this?

Footsteps was essentially my introduction to 3D programming back when I was a wee lad. It's written in legacy OpenGL
(utilizing display lists) and is more or less comprised of a heightmap renderer and a Wavefront model parser and
renderer.

Here's the rough concept for the game as I remember it: The player character is a scientist assisting with some sort of
sci-fi experiment within an enclosure. During the intro cutscene, the player character is communicating with two other
scientists via walkie-talkie when something goes wrong and a dinosaur is teleported (time-traveled, I think?) into the
enclosure. The dinosaur kills the other scientists and the player must now somehow send the dinosaur back (maybe by
activating generators or some other sort of device?) while also avoiding it. The name "Footsteps" was derived from the
envisioned mechanic of footsteps growing louder as the dinosaur moved closer towards you (or vice versa).

For some context, this was around the time that _Slender: The Eight Pages_ was making the rounds and in my opinion this
influence is even apparent in this basic demo.

I got as far as getting a couple of friends together to record voice lines, but in hindsight I obviously didn't have
anything close to the technical knowledge or skill required to put together anything other than a walking simulator.
Still, I can't help but feel a little bit nostalgic just for how exciting it was to be making something that was really
cool to me at the time.

## Modifications

Apparently around a year and a half after my initial work on this demo had stopped, I did some work to convert it to a
Maven project and to get it running on my new computer. Much more recently, I've ported it to LWJGL 3 (to allow the demo
to be run on newer Java versions) and removed the dependency on Slick (which has a hard dependency on Java Web Start).
Apart from these changes and some fixes for things that were outright broken and had been working before, this project
is basically unmodified from how I left it at age 15.

## Assets

I do not own the rights to any assets contained by this repository, nor do I have any sort of record of where the assets
came from. I have reason to suspect that the models and textures were under some sort of permissive license given that
I was able to obtain them in the first place without paying, but again, I really have no clue where they came from. If
you believe you hold the copyright to anything in this repository, please contact me
[via email](mailto:mproncace@protonmail.com).

## License

All code in this repository is released under the MIT License. You are free to use and redistribute it within the bounds
of the license.

As mentioned above, I do not hold copyright over assets included in this repository and so they are not necessarily
encompassed by this license.
