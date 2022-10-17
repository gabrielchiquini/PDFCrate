package pdfcrate.util

/**
 * Defines sizing styles for disposing components
 */
enum class Sizing {
    /**
     * Describes an absolute size in pixels
     */
    ABSOLUTE,

    /**
     * Describes a size proportional to the available space in the container
     */
    PROPORTIONAL,

    /**
     * Describes a size that uses the children components to define the parent size,
     * keeping it the smallest possible
     */
    SHRINK,
}
